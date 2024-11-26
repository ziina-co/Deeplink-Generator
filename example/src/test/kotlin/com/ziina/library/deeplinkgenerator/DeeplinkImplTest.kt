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

import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.Description
import io.qameta.allure.Attachment
import io.qameta.allure.Step
import org.junit.Assert.assertEquals
import org.junit.Test

@Epic("Deeplink Parsing")
@Feature("Deeplink Implementation Tests")
class DeeplinkImplTest {

    @Test
    @Story("Parse Example1 Deeplink")
    @Description("Test to verify Example1 deeplink parsing from URL")
    fun testExample1() {
        val url = "https://example.com/example1"
        val deeplink = parseDeeplink("Example1", url)
        assertEquals(DeeplinksImpl.Example1, deeplink)
    }

    @Test
    @Story("Parse Example2 Deeplink")
    @Description("Test to verify Example2 deeplink parsing from URL")
    fun testExample2() {
        val url = "https://example.com/example2"
        val deeplink = parseDeeplink("Example2", url)
        assertEquals(DeeplinksImpl.Example2, deeplink)
    }

    @Test
    @Story("Parse Example3 Deeplink")
    @Description("Test to verify Example3 deeplink parsing with parameters")
    fun testExample3() {
        val url = "https://example.com/example3/123/details/John"
        val deeplink = parseDeeplink("Example3", url)
        assertEquals(DeeplinksImpl.Example3("123", "John"), deeplink)
    }

    @Test
    @Story("Parse Example4 Deeplink")
    @Description("Test to verify Example4 deeplink parsing with locale and query parameters")
    fun testExample4() {
        val url = "https://example.com/en/example4?name=John&surname=Doe&address=123%20Main%20St"
        val deeplink = parseDeeplink("Example4", url)
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
    @Story("Parse Example5 Deeplink")
    @Description("Test to verify Example5 deeplink parsing with specific host")
    fun testExample5() {
        val url = "https://pay.example.com/123"
        val deeplink = parseDeeplink("Example5", url)
        assertEquals(DeeplinksImpl.Example5("123"), deeplink)
    }

    @Test
    @Story("Fail Example5 Deeplink with wrong host")
    @Description("Test to verify that Example5 parsing fails with an incorrect host")
    fun testExample5WrongHost() {
        val url = "https://example.com/123"
        val deeplink = parseDeeplink("Example5", url)
        assertEquals(null, deeplink)
    }

    @Step("Parsing deeplink for {name} from URL: {url}")
    private fun parseDeeplink(name: String, url: String): DeeplinksImpl? {
        return when (name) {
            "Example1" -> DeeplinksImpl.Example1.fromUrl(url)
            "Example2" -> DeeplinksImpl.Example2.fromUrl(url)
            "Example3" -> DeeplinksImpl.Example3.fromUrl(url)
            "Example4" -> DeeplinksImpl.Example4.fromUrl(url)
            "Example5" -> DeeplinksImpl.Example5.fromUrl(url)
            else -> null
        }
    }
}