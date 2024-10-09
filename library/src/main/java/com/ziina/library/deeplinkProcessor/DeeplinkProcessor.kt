package com.ziina.library.deeplinkProcessor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.ziina.library.deeplinkProcessor.DeeplinkProcessor.PathSegment.Literal
import com.ziina.library.deeplinkProcessor.DeeplinkProcessor.PathSegment.Placeholder
import com.ziina.library.deeplinkProcessor.annotations.Deeplink
import com.ziina.library.deeplinkProcessor.annotations.DeeplinkRoot
import java.util.Locale

class DeeplinkProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        checkNotNull(deeplinkRootFQDN) { "DeeplinkRoot annotation not found" }
        checkNotNull(deeplinkAnnotationSimpleName) { "Deeplink annotation not found" }

        val symbols = resolver.getSymbolsWithAnnotation(deeplinkRootFQDN)

        logger.info("$TAG Found ${symbols.count()} classes with DeeplinkRoot annotation")

        symbols.forEach { symbol ->
            if (symbol is KSClassDeclaration) {
                processDeeplinkRoot(symbol)
            }
        }

        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun processDeeplinkRoot(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.containingFile?.packageName?.asString()
            ?: throw IllegalStateException("Package name not found")

        val className = classDeclaration.simpleName.asString()

        val deeplinkRootAnnotation =
            classDeclaration.getAnnotationsByType(DeeplinkRoot::class).firstOrNull()
                ?: throw IllegalStateException("DeeplinkRoot annotation not found for the class $className")

        val schemas: Array<String> = deeplinkRootAnnotation.schemas
        val defaultHosts: Array<String> = deeplinkRootAnnotation.defaultHosts

        val outputFileName = "${className}Impl"

        val fileContent = buildString {
            appendLine(
                """
                package $packageName
                
                import java.net.URL
                import java.net.URLDecoder
                
                sealed interface $outputFileName {
            """.trimIndent()
            )

            val classes = classDeclaration
                .getAllFunctions()
                .mapNotNull { processFunction(schemas, defaultHosts, it) }
                .toList()

            append("    companion object {\n")
            generateParseFunctionForParentClass(outputFileName, classes, schemas)
            appendLine("    }")
            append("}")
        }

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = packageName,
            fileName = outputFileName
        ).use { outputStream ->
            outputStream.write(fileContent.toByteArray())
        }
    }

    @OptIn(KspExperimental::class)
    private fun StringBuilder.processFunction(
        schemas: Array<String>,
        defaultHosts: Array<String>,
        function: KSFunctionDeclaration
    ): ProcessedClass? {
        val functionName = function.simpleName
        val className = functionName.asString().capitalize()

        logger.info("$TAG Processing function $className")
        val deeplinkAnnotation = function.getAnnotationsByType(Deeplink::class).firstOrNull()

        if (deeplinkAnnotation == null) {
            logger.warn("$TAG Deeplink annotation not found for the function $className")

            return null
        }

        val extendName = function.parentDeclaration?.simpleName?.asString()
            ?: throw IllegalStateException("Parent declaration not found for the function $className")

        val parameters = function
            .parameters
            .map { parameter ->
                val parameterName = parameter.name?.asString() ?: throw IllegalArgumentException(
                    "Parameter name not found for the parameter located" +
                            " in ${parameter.location} in function $className"
                )

                Parameter(parameterName, !parameter.type.resolve().isMarkedNullable)
            }
            .toSet()

        val pathArguments = deeplinkAnnotation
            .path
            .substringBefore("?")
            .split("/")
            .mapNotNull { segment ->
                when {
                    segment.isBlank() -> null

                    segment.first() == '{' && segment.last() == '}' ->
                        Placeholder(segment.substring(1..segment.length - 2))

                    else -> Literal(segment)
                }
            }

        val hosts = deeplinkAnnotation
            .hosts
            .ifEmpty { defaultHosts }
            .toSet()

        appendLine(
            """
            |    /**
            |     * Generated class for the deeplink function 
            |     * @see ${function.qualifiedName?.asString() ?: function.simpleName.asString()}
            |     * deeplink example: ${schemas.firstOrNull()}://${hosts.firstOrNull()}${deeplinkAnnotation.path}
            |     */
            """.trimMargin()
        )

        if (parameters.isEmpty()) {
            appendLine("    data object $className : ${extendName}Impl {")
            appendParseFunctionForChildClass(
                hosts,
                pathArguments,
                className,
                parameters,
                indent = "        "
            )
            appendLine("    }")
        } else {
            append("    data class $className(\n")

            function.parameters.forEach { param ->
                val paramName = param.name?.asString()
                val paramType = param.type.resolve().declaration.qualifiedName?.asString()
                append("        val $paramName: $paramType")
                if (param.type.resolve().isMarkedNullable) append("?")
                append(",\n")
            }

            appendLine("    ) : ${extendName}Impl {")
            appendLine("        companion object {")
            appendParseFunctionForChildClass(
                hosts,
                pathArguments,
                className,
                parameters,
                indent = "            "
            )
            appendLine("        }")
            appendLine("    }")
        }

        appendLine()

        return ProcessedClass(className)
    }

    private fun StringBuilder.appendParseFunctionForChildClass(
        hosts: Set<String>,
        pathArguments: List<PathSegment>,
        className: String,
        parameters: Set<Parameter>,
        indent: String,
    ) {
        val hostsString = "listOf(" + hosts.joinToString(", ") { "\"$it\"" } + ")"

        appendLine(
            """
          |${indent}fun fromUrl(url: String): $className? {
            |$indent    val parsedUrl = parseUrl(url) ?: return null
            |
            |$indent    if (parsedUrl.host !in $hostsString) return null
            |
            |$indent    // try to parse path segments
            |$indent    if (parsedUrl.pathSegments.size != ${pathArguments.size}) return null
            |
        """.trimMargin()
        )

        pathArguments.forEachIndexed { index, pathSegment ->
            when (pathSegment) {
                is Literal -> {
                    appendLine(
                        "$indent    if (parsedUrl.pathSegments.getOrNull($index) != \"${pathSegment.value}\") " +
                                "return null\n"
                    )
                }

                is Placeholder -> {
                    appendLine(
                        "$indent    val ${pathSegment.name} = parsedUrl.pathSegments.getOrNull($index) " +
                                "?: return null\n"
                    )
                }
            }
        }

        val placeholderParamNames = pathArguments.filterIsInstance<Placeholder>().map { it.name }
        appendLine("$indent    // try to parse query parameters")

        parameters
            .filter { it.name !in placeholderParamNames }
            .forEach { (param, isMandatory) ->
                val unwrap = if (isMandatory) "?: return null" else ""

                appendLine("$indent    val $param = parsedUrl.pathArguments[\"$param\"]$unwrap")
            }

        appendLine()

        if (parameters.isEmpty()) {
            appendLine("$indent    return $className")
        } else {
            appendLine("$indent    return $className(")

            parameters.forEach { (param, _) ->
                appendLine("$indent        $param = $param,")
            }

            appendLine("$indent    )")
        }

        appendLine("$indent}")
    }

    private fun StringBuilder.generateParseFunctionForParentClass(
        outputFileName: String,
        classes: List<ProcessedClass>,
        schemas: Array<String>,
    ) {
        val schemasString = "listOf(" + schemas.joinToString(", ") { "\"$it\"" } + ")"

        appendLine("        fun parse(url: String): $outputFileName? {")
        appendLine("            val parsedUrl = parseUrl(url) ?: return null\n")
        appendLine("            if (parsedUrl.schema !in $schemasString) return null\n")
        classes.forEach { appendLine("            ${it.value}.fromUrl(url)?.let { return it }\n") }
        appendLine("            return null")
        appendLine("        }\n")

        // Add helper functions
        appendLine(
            """        private fun parseUrl(url: String): ParsedUrl? {
            val urlObj = runCatching { URL(url) }.getOrElse { return null }

            return ParsedUrl(
                schema = urlObj.protocol,
                host = urlObj.host,
                pathSegments = urlObj.path.split("/").filter { it.isNotEmpty() }
                    .map { URLDecoder.decode(it, "UTF-8") },
                pathArguments = urlObj.query?.let { query ->
                    query
                        .split("&")
                        .associate { argument ->
                            val argumentParts = argument.split("=", limit = 2)
                            val key = argumentParts.first()

                            val value = argumentParts.getOrNull(1)
                                ?.let { URLDecoder.decode(it, "UTF-8") }

                            key to value
                        }
                } ?: emptyMap()
            )
        }

        private data class ParsedUrl(
            val schema: String,
            val host: String,
            val pathSegments: List<String>,
            val pathArguments: Map<String, String?>
        )"""
        )
    }

    private fun String.capitalize() =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    companion object {
        private const val TAG = "DeeplinkProcessor: "

        private val deeplinkRootFQDN = DeeplinkRoot::class.qualifiedName
        private val deeplinkAnnotationSimpleName = Deeplink::class.simpleName
    }

    private data class ProcessedClass(val value: String)

    sealed interface PathSegment {
        data class Literal(val value: String) : PathSegment
        data class Placeholder(val name: String) : PathSegment
    }

    private data class Parameter(
        val name: String,
        val isMandatory: Boolean
    )
}