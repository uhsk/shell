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

package io.github.uhsk.shell.shells

import java.nio.charset.Charset

interface IShell {

    val shell: Array<String>

    val charset: Charset

    val concat: String

    fun getExitCodeCommand(uuid: String): String

    fun parseExitCode(line: String): Int?

}