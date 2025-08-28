package com.github.xepozz.git_churn.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.suspendCoroutine

@Service(Service.Level.PROJECT)
class MyProjectService(
    val project: Project,
    val coroutineScope: CoroutineScope,
) {
    var result: GitLogResult? = null

    fun findDescriptor(virtualFile: VirtualFile) = result?.filesInfo[virtualFile]

    fun callGitLog() {
        val command = GeneralCommandLine(
            listOf(
                "git",
                "log",
                "--all",
                "-M",
                "-C",
                "--name-only",
//                "--format='format:'",
//                format,
            )
        )

        val projectBasePath = project.basePath
        command.workDirectory = projectBasePath?.let { File(it) }

        println("command is: ${command.commandLineString}")

        coroutineScope.launch(Dispatchers.IO) {
            val result = executeCommand(command)

            this@MyProjectService.result = result
            val parsed = parseGitLogOutput(result)
            result.filesInfo = parsed

            println("log: $parsed")
            println("exit: ${result.exitCode}")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun executeCommand(command: GeneralCommandLine): GitLogResult =
        suspendCoroutine { continuation ->
            val processHandler = OSProcessHandler(command)
            val buffer = StringBuilder()

            processHandler.addProcessListener(object : ProcessListener {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    if (outputType === ProcessOutputTypes.STDOUT || outputType === ProcessOutputTypes.STDERR) {
                        buffer.append(event.text)
                    }
                }

                override fun processTerminated(event: ProcessEvent) {
                    val result = GitLogResult(
                        output = buffer.toString(),
                        exitCode = event.exitCode
                    )

                    continuation.resumeWith(Result.success(result))
                }
            })

            processHandler.startNotify()
        }

    private fun parseGitLogOutput(result: GitLogResult): Map<VirtualFile, FileNodeDescriptor> {
        val projectDir = project.guessProjectDir() ?: return emptyMap()

        return result.output
            .lines()
            .filter { it.isNotBlank() && !it.startsWith(" ") }
            .groupingBy { it }
            .eachCount()
            .mapNotNull { (path, count) ->
                FileNodeDescriptor(
                    projectDir.findFileByRelativePath(path) ?: return@mapNotNull null,
                    count
                )
            }
            .associateBy { it.path }
    }
}