package com.github.xepozz.git_churn

import com.github.xepozz.git_churn.config.GitChurnConfigSettings
import com.github.xepozz.git_churn.notification.NotificationUtil
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import org.apache.commons.io.FilenameUtils

object OutputParser {
    private val settings by lazy { GitChurnConfigSettings.getInstance() }

    fun parseGitLogOutput(project: Project, output: ProcessOutput): GitChurnDescriptor {
        val projectDir = project.guessProjectDir() ?: return GitChurnDescriptor.EMPTY
        if (output.exitCode != 0) {
            NotificationUtil.sendNotification(project, "Git Churn Error", output.stderr)
            return GitChurnDescriptor.EMPTY
        }
        val stdout = output.stdout

        val fileChanges = stdout
            .lines()
            .groupingBy { it }
            .eachCount()

        val result = GitChurnDescriptor()

        fileChanges.forEach { (path, count) ->
            val virtualFile = projectDir.findFileByRelativePath(path)
            if (virtualFile != null && virtualFile.exists()) {
                result.maxCount = maxOf(result.maxCount, count)
                result.filesInfo[virtualFile] = FileNodeDescriptor(
                    changeCount = count,
                )
            }
        }

        filterIgnored(result, project)
        addDirectoriesWithChurn(result, projectDir)

        return result
    }

    private fun filterIgnored(
        result: GitChurnDescriptor,
        project: Project
    ) {
        val projectPath = "${project.basePath}/"
        result.filesInfo = result.filesInfo
            .filter { fileInfo ->
                val filePath = fileInfo.key.path.substringAfter(projectPath)

                !settings.excludePatterns.any { FilenameUtils.wildcardMatch(filePath, it) }
            }.toMutableMap()
    }

    private fun addDirectoriesWithChurn(result: GitChurnDescriptor, projectDir: VirtualFile) {
        val filesInfo = result.filesInfo
        var maxCount = result.maxCount

        val allDirectories = mutableSetOf<VirtualFile>()

        filesInfo.keys.forEach { file ->
            var currentDir = file.parent

            while (currentDir != null && currentDir.path.startsWith(projectDir.path) && currentDir != projectDir) {
                if (allDirectories.contains(currentDir)) {
                    break
                }
                allDirectories.add(currentDir)
                currentDir = currentDir.parent
            }
        }

        allDirectories
            .sortedByDescending { it.path }
            .forEach { directory ->
                val changeCount = findMaxChangeCountForDirectory(directory, filesInfo)
                maxCount = maxOf(maxCount, changeCount)
                filesInfo[directory] = FileNodeDescriptor(
                    changeCount = changeCount,
                )
            }

        result.filesInfo = filesInfo
        result.maxCount = maxCount
    }

    private fun findMaxChangeCountForDirectory(
        directory: VirtualFile,
        filesInfo: Map<VirtualFile, FileNodeDescriptor>
    ): Int {
        return filesInfo
            .filter { it.key.parent == directory }
            .maxOfOrNull { it.value.changeCount }
            ?: 0
    }
}
