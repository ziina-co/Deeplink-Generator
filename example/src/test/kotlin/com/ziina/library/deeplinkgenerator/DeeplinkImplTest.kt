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

import org.junit.Assert.assertEquals
import org.junit.Test

class DeeplinkImplTest {
    @Test
    fun testExample1() {
        val deeplink = DeeplinksImpl
            .Example1
            .fromUrl("https://example.com/example1")

        assertEquals(DeeplinksImpl.Example1, deeplink)
    }

    @Test
    fun testExample2() {
        val deeplink = DeeplinksImpl
            .Example2
            .fromUrl("https://example.com/example2")

        assertEquals(DeeplinksImpl.Example2, deeplink)
    }

    @Test
    fun testExample3() {
        val deeplink = DeeplinksImpl
            .Example3
            .fromUrl("https://example.com/example3/123/details/John")

        assertEquals(DeeplinksImpl.Example3("123", "John"), deeplink)
    }

    @Test
    fun testExample4() {
        val deeplink = DeeplinksImpl
            .Example4
            .fromUrl("https://example.com/en/example4?name=John&surname=Doe&address=123%20Main%20St")

        assertEquals(
            DeeplinksImpl.Example4(
                locale = "en",
                "John",
                "Doe",
                "123 Main St"
            ),
            deeplink
        )
    }

    @Test
    fun testExample5() {
        val deeplink = DeeplinksImpl
            .Example5
            .fromUrl("https://pay.example.com/123")

        assertEquals(DeeplinksImpl.Example5("123"), deeplink)
    }

    @Test
    fun testExample5WrongHost() {
        val deeplink = DeeplinksImpl
            .Example5
            .fromUrl("https://example.com/123")

        assertEquals(null, deeplink)
    }
}