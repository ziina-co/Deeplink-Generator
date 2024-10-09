package com.ziina.library.deeplinkProcessor.annotations

/**
 * Annotation to mark the deeplink function
 *
 * @param path The path part of the deeplink Url with placeholders for parameters
 * for example if you have a deeplink like "https://app.ziina.com/profile/{userId}?tab={tab}"
 * the path should be "/profile/{userId}?tab={tab}"
 *
 * @param hosts The hosts that this deeplink should be matched against,
 * if not provided it will be matched against the hosts provided in the DeeplinkRoot annotation
 * if provided hosts from DeeplinkRoot will be ignored
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Deeplink(
    val path: String,
    val hosts: Array<String> = [],
)