package com.github.xepozz.git_churn.services

import com.intellij.openapi.vfs.VirtualFile

data class GitLogResult(
    val output: String,
    val exitCode: Int,
    val isSuccess: Boolean = exitCode == 0,
    var filesInfo: Map<VirtualFile, FileNodeDescriptor> = emptyMap()
)