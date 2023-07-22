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

import io.github.uhsk.shell.shells.*
import io.github.uhsk.shell.threads.StreamReaderThread
import java.io.DataOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import java.util.logging.Logger

/**
 * @param iShell shell
 * @param directory shell directory
 * @param env shell environment
 * @see IShell
 */
class Shell(private val iShell: IShell, directory: File?, env: Map<String, String>) {

    companion object {

        @JvmOverloads
        fun PoserShell(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplPowerShell(), directory, env)
        }

        @JvmOverloads
        fun Cmd(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplCmd(), directory, env)
        }

        @JvmOverloads
        fun Bash(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplBash(), directory, env)
        }

        @JvmOverloads
        fun Sh(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplSh(), directory, env)
        }

        @JvmOverloads
        fun Su(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplSu(), directory, env)
        }

        @JvmOverloads
        fun GitBash(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplGitBash(), directory, env)
        }

        @JvmOverloads
        fun Zsh(directory: File? = null, env: Map<String, String> = mapOf()): Shell {
            return Shell(ImplZsh(), directory, env)
        }
    }

    private val process: Process
    private val readerThread: StreamReaderThread
    private val inputStream: StandardInputStream

    init {
        val processBuilder = ProcessBuilder(*iShell.shell)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(directory)
        process = processBuilder.start()

        inputStream = StandardInputStream(process.outputStream)
        readerThread = StreamReaderThread("StreamReaderThread", process.inputStream, iShell.charset)
        readerThread.start()
    }

    /**
     * 运行命令
     */
    fun run(command: String): Result = run(Command(command))

    /**
     * 运行命令
     */
    fun run(vararg command: String): Result = run(Command(command.joinToString(" ")))

    /**
     * 运行命令
     *
     * @param command command
     */
    fun run(command: Command): Result {
        val result = Result()
        val countDownLatch = java.util.concurrent.CountDownLatch(1)
        readerThread.onLineCallback = java.util.function.Function {
            result.appendOutput(it)
            val exitCode: Int? = iShell.parseExitCode(it)
            if (exitCode != null) {
                result.exitCode = exitCode
                countDownLatch.countDown()
            }
            command.onLineOutputCallback?.apply(process.outputStream, it)
        }

        val runCommand = command.command
        inputStream.writeBytes(runCommand, command.charset)
        inputStream.writeNewLine()
        Thread.sleep(100)

        val exitCodeCommand: String = iShell.getExitCodeCommand(command.uuid.toString())
        inputStream.writeBytes(exitCodeCommand, Charsets.UTF_8)
        inputStream.writeNewLine()

        if (!countDownLatch.await(command.timeout, TimeUnit.MILLISECONDS)) {
            throw java.util.concurrent.TimeoutException()
        }

        return result
    }

    /**
     * 中断
     */
    fun interrupt() {
        inputStream.write(3)
        inputStream.flush()
    }

    /**
     * 退出
     */
    fun exit() {
        readerThread.interrupt()
        process.destroy()
    }

    /**
     * 执行命令相关参数
     */
    data class Command(
        /**
         * 需要执行的命令
         */
        val command: String,

        /**
         * 命令唯一标识
         * 用于获取命令执行结果
         */
        var uuid: UUID = UUID.randomUUID(),

        /**
         * 命令字符集
         */
        var charset: Charset = Charsets.US_ASCII,

        /**
         * 命令超时时间 (毫秒)
         * 默认 1 天
         */
        var timeout: Long = TimeUnit.DAYS.toMillis(1),

        /**
         * 命令输出回调
         * @param OutputStream 输出流 (可用于向命令输入)
         * @param String       输出的一行内容
         * @see Process.getOutputStream
         */
        var onLineOutputCallback: BiFunction<OutputStream, String, Unit>? = null
    )

    /**
     * 命令执行结果
     */
    class Result {
        companion object {
            /**
             * 命令执行成功的退出码
             */
            const val SUCCESS_EXIT_CODE = 0

            /**
             * 命令执行失败的退出码
             */
            const val UNKNOWN_EXIT_CODE = -1
        }

        /**
         * 命令退出码
         */
        var exitCode: Int = UNKNOWN_EXIT_CODE
            internal set

        /**
         * 命令是否执行成功
         */
        val isSuccess: Boolean
            get() = exitCode == SUCCESS_EXIT_CODE

        /**
         * 命令输出
         */
        private val _output = LinkedList<String>()

        /**
         * 命令输出
         */
        val stdout: List<String>
            get() = _output

        fun appendOutput(output: String) {
            _output.add(output)
        }

    }

    private class StandardInputStream(stream:OutputStream): DataOutputStream(stream) {
        fun writeBytes(s: String, charset: Charset = Charsets.UTF_8) {
            write(s.toByteArray(charset))
        }
        fun writeNewLine() {
            writeByte('\n'.code)
            flush()
        }
    }
}