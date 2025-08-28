package com.github.xepozz.git_churn

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir

object OutputParser {
    fun parseGitLogOutput(project: Project, output: String): GitChurnDescriptor {
        val projectDir = project.guessProjectDir() ?: return GitChurnDescriptor.EMPTY

        return GitChurnDescriptor().apply {
            filesInfo = output
                .lines()
                .filter { it.isNotBlank() && !it.startsWith(" ") }
                .groupingBy { it }
                .eachCount()
                .mapNotNull { (path, count) ->
                    maxCount = maxOf(maxCount, count)
                    FileNodeDescriptor(
                        projectDir.findFileByRelativePath(path) ?: return@mapNotNull null,
                        count
                    )
                }
                .associateBy { it.path }
        }
    }
}