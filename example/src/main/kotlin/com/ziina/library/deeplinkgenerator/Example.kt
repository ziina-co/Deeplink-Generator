/*
 * Copyright 2024 Ziina FZ-LLC.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Author: Anton Dudakov
 *
 */

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