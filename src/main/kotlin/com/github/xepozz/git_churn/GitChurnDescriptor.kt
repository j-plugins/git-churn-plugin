package com.github.xepozz.git_churn

import com.intellij.openapi.vfs.VirtualFile

data class GitChurnDescriptor(
    var filesInfo: MutableMap<VirtualFile, FileNodeDescriptor> = mutableMapOf(),
    var maxCount: Int = 0,
) {
    companion object {
        val EMPTY = GitChurnDescriptor()
    }
}

data class FileNodeDescriptor(
    val changeCount: Int,
)