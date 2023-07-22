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

import io.github.uhsk.shell.Shell
import java.nio.charset.Charset

open class ImplSh : IShell {

    private val _charset = Charset.forName("UTF-8")
    private val _pattern = Regex(pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}=(\\d+)")

    override val shell: Array<String>
        get() = arrayOf("sh")

    override val charset: Charset
        get() = _charset

    override val concat: String
        get() = ";"

    override fun getExitCodeCommand(uuid: String): String {
        return "echo $uuid=\$?"
    }

    override fun parseExitCode(line: String): Int? {
        val matchResult: MatchResult = _pattern.find(line) ?: return null
        return matchResult.groups[1]?.value?.toIntOrNull() ?: Shell.Result.UNKNOWN_EXIT_CODE
    }
}