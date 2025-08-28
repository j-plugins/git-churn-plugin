package com.github.xepozz.git_churn

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.suspendCoroutine

@Service(Service.Level.PROJECT)
class GitChurnService(
    val project: Project,
) {
    val settings: GitChurnSettings = project.getService(GitChurnSettings::class.java)
    var result = GitChurnDescriptor.EMPTY

    fun findDescriptor(virtualFile: VirtualFile) = result.filesInfo[virtualFile]

    suspend fun refresh() {
        val format = when {
            settings.duration1Month -> """--since="1 months ago""""
            settings.duration3Months -> """--since="3 months ago""""
            settings.duration6Months -> """--since="6 months ago""""
            settings.duration1Year -> """--since="1 year ago""""
            else -> "--all"
        }

        val command = GeneralCommandLine(
            listOf(
                "git",
                "log",
                "--all",
                "-M",
                "-C",
                "--name-only",
                "--format=format:",
                format,
            )
        )

        val projectBasePath = project.basePath
        command.workDirectory = projectBasePath?.let { File(it) }

//        println("command is: ${command.commandLineString}")

        withContext(Dispatchers.IO) {
            val gitLogResult = executeCommand(command)

            result = OutputParser.parseGitLogOutput(project, gitLogResult.output)

//            println("log: $gitLogResult")
//            println("churn: $result")
        }
    }

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
}

data class GitLogResult(
    val output: String,
    val exitCode: Int,
    val isSuccess: Boolean = exitCode == 0,
)