package com.github.xepozz.git_churn.services

import com.intellij.openapi.vfs.VirtualFile

data class FileNodeDescriptor(
    val path: VirtualFile,
    val changeCount: Int
)