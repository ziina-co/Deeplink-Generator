package com.ziina.library.deeplinkgenerator

import com.ziina.library.deeplinkProcessor.annotations.Deeplink
import com.ziina.library.deeplinkProcessor.annotations.DeeplinkRoot

@DeeplinkRoot(
    schemas = ["https", "example"],
    defaultHosts = ["example.com", "www.example.com"]
)
interface Deeplinks {
    @Deeplink(path = "/example1")
    fun example1()

    @Deeplink(path = "/example2")
    fun example2()

    @Deeplink(path = "/example3/{id}/details/{name}")
    fun example3(id: String, name: String)

    @Deeplink(path = "/{locale}/example4?name={name}&surname={surname}&address={address}")
    fun example4(locale: String, name: String, surname: String, address: String)

    @Deeplink(path = "/{paymentId}", hosts = ["pay.example.com"])
    fun example5(paymentId: String)
}