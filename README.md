# Deeplink-Generator

## Installation

add the following dependencies to your gradle file:

```gradle
implementation("com.ziina.library:deeplinkgenerator:<version>")
ksp("com.ziina.library:deeplinkgenerator:<version>")
```

## Usage

### Declare deeplinks

To declare deeplinks you need to create an interface and annotate it with `@DeeplinkRoot`

```kotlin
@DeeplinkRoot(
    schemas = ["https", "example"],
    defaultHosts = ["example.com", "www.example.com"]
)
interface Deeplinks
```

### Declare deeplink

To declare a deeplink you need to create a function and annotate it with `@Deeplink`

```kotlin
@Deeplink(path = "/example1")
fun example1()
```

You can also add parameters to the deeplink

```kotlin
@Deeplink(path = "/example3/{id}/details/{name}")
fun example3(id: String, name: String)
```

And specify custom hosts list
IMPORTANT NOTE: if `hosts` field is specified in the annotation, the `defaultHosts` field in
the `@DeeplinkRoot` annotation will be IGNORED

```kotlin
@Deeplink(path = "/{paymentId}", hosts = ["pay.example.com"])
fun example5(paymentId: String)
```

### Generated file

Code processor will generate:

1. a sealed interface named as the name and a package of the interface annotated with
   the `@DeeplinkRoot` with the suffix `Impl`;
2. a function to parse the deeplink in the generated interface that will return the
   proper `data class` or `data object`;
3. for each deeplink declared in the interface annotated with `@Deeplink` a `data class`
   with the parameters declared in the function or a `data object` if no parameters are declared.

example:

```kotlin

sealed interface DeeplinksImpl {
    data object Example1 : DeeplinksImpl {
        fun fromUrl(url: String): Example1? {
            //...
        }
    }

    data object Example3 : DeeplinksImpl {
        companion object {
            fun fromUrl(url: String): Example3? {
                //...
            }
        }
    }

    data object Example5 : DeeplinksImpl {
        companion object {
            fun fromUrl(url: String): Example5? {
                //...
            }
        }
    }

    companion object {
        fun parse(url: String): DeeplinksImpl? {
            val parsedUrl = parseUrl(url) ?: return null
            if (parsedUrl.schema !in listOf("https", "example")) return null
            Example1.fromUrl(url)?.let { return it }
            Example3.fromUrl(url)?.let { return it }
            Example5.fromUrl(url)?.let { return it }

            return null
        }
    }
}
```

### Usage

To use the generated code you can call the `parse` function from the generated interface, and then
use `when` function on returned object to handle the deeplink properly

```kotlin
fun handleUrl(url: String) {
   val deeplink = DeeplinksImpl.parse(url) ?: return

   when (deeplink) {
      DeeplinksImpl.Example1 -> {
         // navigate to example1 screen
      }

      is DeeplinksImpl.Example3 -> {
         // navigate to example3 screen with params id and name
         println(deeplink.id)
         println(deeplink.name)
      }
     
      is DeeplinksImpl.Example5 -> {
         // don't navigate but save params into the database
         println(deeplink.paymentId)
      }
   }
}
```