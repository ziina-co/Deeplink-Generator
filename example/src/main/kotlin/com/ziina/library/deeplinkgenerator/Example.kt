package com.ziina.library.deeplinkgenerator

fun handleUrl(url: String) {
    val deeplink = DeeplinksImpl.parse(url) ?: return

    when (deeplink) {
        DeeplinksImpl.Example1 -> {
            // navigate to example1 screen
        }

        DeeplinksImpl.Example2 -> {
            // navigate to example2 screen
        }

        is DeeplinksImpl.Example3 -> {
            // navigate to example3 screen with params id and name
            println(deeplink.id)
            println(deeplink.name)
        }

        is DeeplinksImpl.Example4 -> {
            // don't navigate but save params into the database
            println(deeplink.locale)
            println(deeplink.name)
            println(deeplink.surname)
            println(deeplink.address)
        }
        is DeeplinksImpl.Example5 -> {
            // don't navigate but save params into the database
            println(deeplink.paymentId)
        }
    }
}