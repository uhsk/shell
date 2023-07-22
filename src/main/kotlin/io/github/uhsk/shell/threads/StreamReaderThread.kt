/*
 * Copyright (C) 2023 Sollyu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.uhsk.shell.threads

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.function.Function

class StreamReaderThread(name: String, inputStream: InputStream, charset: Charset) : Thread(name) {

    private val bufferReader = BufferedReader(InputStreamReader(inputStream, charset))

    var onLineCallback: Function<String, Unit>? = null

    override fun run() = bufferReader.forEachLine { line ->
        onLineCallback?.apply(line)
    }

}