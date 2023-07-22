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

open class ImplPowerShell: IShell {

    private val _charset = Charset.forName("GBK")
    private val _pattern = Regex(pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}=(?i)(True|False)=(\\d*)")

    override val shell: Array<String>
        get() = arrayOf("powershell")

    override val charset: Charset
        get() = _charset

    override val concat: String
        get() = ";"

    override fun getExitCodeCommand(uuid: String): String {
        return "echo $uuid=$?=\$LastExitCode"
    }

    override fun parseExitCode(line: String): Int? {
        val matchResult: MatchResult = _pattern.find(line) ?: return null
        return when (matchResult.groups[1]?.value) {
            "True" -> matchResult.groups[2]?.value?.toIntOrNull() ?: Shell.Result.SUCCESS_EXIT_CODE
            "False" -> matchResult.groups[2]?.value?.toIntOrNull()?.unaryMinus() ?: Shell.Result.UNKNOWN_EXIT_CODE
            else -> null
        }
    }
}