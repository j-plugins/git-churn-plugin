package com.github.xepozz.git_churn

import com.intellij.openapi.vfs.VirtualFile

data class GitChurnDescriptor(
    var filesInfo: Map<VirtualFile, FileNodeDescriptor> = emptyMap(),
    var maxCount: Int = 0,
) {
    companion object {
        val EMPTY = GitChurnDescriptor()
    }
}

data class FileNodeDescriptor(
    val path: VirtualFile,
    val changeCount: Int
)