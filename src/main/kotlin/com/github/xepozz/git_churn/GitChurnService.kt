package com.github.xepozz.git_churn

import com.github.xepozz.git_churn.config.GitChurnConfigSettings
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File

@Service(Service.Level.PROJECT)
class GitChurnService(
    val project: Project,
) {
    private val settings by lazy { GitChurnConfigSettings.getInstance() }
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
                "-M",
                "-C",
                "--name-only",
                "--format=",
                format,
            )
        )

        val projectBasePath = project.basePath
        command.workDirectory = projectBasePath?.let { File(it) }

//        println("command is: ${command.commandLineString}")

        val gitLogResult = withContext(Dispatchers.IO) { executeCommand(command) }

        result = OutputParser.parseGitLogOutput(project, gitLogResult.output)
    }

    private suspend fun executeCommand(command: GeneralCommandLine): GitLogResult =
        suspendCancellableCoroutine { continuation ->
            val processHandler = OSProcessHandler(command)
            val processOutput = ProcessOutput()

            processHandler.addProcessListener(CapturingProcessAdapter(processOutput))
            processHandler.addProcessListener(object : ProcessListener {
                override fun processTerminated(event: ProcessEvent) {
                    val result = GitLogResult(
                        output = processOutput,
                        exitCode = event.exitCode
                    )

                    continuation.resumeWith(Result.success(result))
                }
            })

            processHandler.startNotify()
        }
}

data class GitLogResult(
    val output: ProcessOutput,
    val exitCode: Int,
)