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

package io.github.uhsk.shell

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShellTest {

    @Test
    fun run_test_cat() {
        val shell = Shell.Bash()
        val result = shell.run("cat", "src/test/resources/test.txt")
        assertEquals(Shell.Result.SUCCESS_EXIT_CODE, result.exitCode)
        assertTrue(result.stdout.any { it == "test in txt file" })
        shell.exit()
    }

    @Test
    fun run_test_fail() {
        val shell = Shell.Bash()
        val result = shell.run("fail")
        assertEquals(127, result.exitCode)
        shell.exit()
    }

    @Test
    fun run_test_sh() {
        val shell = Shell.Sh()
        val result = shell.run("echo", "\"this is a test\"")
        assertEquals(Shell.Result.SUCCESS_EXIT_CODE, result.exitCode)
        assertTrue(result.stdout.any { it == "this is a test" })

        val result2 = shell.run("ping -c 1 www.baidu.com")
        assertEquals(Shell.Result.SUCCESS_EXIT_CODE, result2.exitCode)

        shell.exit()
    }

}