package com.github.xepozz.git_churn

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

object OutputParser {
    fun parseGitLogOutput(project: Project, output: String): GitChurnDescriptor {
        val projectDir = project.guessProjectDir() ?: return GitChurnDescriptor.EMPTY

        val fileChanges = output
            .lines()
            .filter { it.isNotBlank() && !it.startsWith(" ") }
            .groupingBy { it }
            .eachCount()

        val result = GitChurnDescriptor()

        fileChanges.forEach { (path, count) ->
            val virtualFile = projectDir.findFileByRelativePath(path)
            if (virtualFile != null && virtualFile.exists()) {
                result.maxCount = maxOf(result.maxCount, count)
                result.filesInfo[virtualFile] = FileNodeDescriptor(
                    path = virtualFile,
                    changeCount = count
                )
            }
        }

        addDirectoriesWithChurn(result, projectDir)

        return result
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
                    path = directory,
                    changeCount = changeCount
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
