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