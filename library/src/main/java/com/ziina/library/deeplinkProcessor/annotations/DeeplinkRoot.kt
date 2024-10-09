package com.ziina.library.deeplinkProcessor.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeeplinkRoot(val schemas: Array<String>, val defaultHosts: Array<String>)