package com.github.xepozz.git_churn

import com.github.xepozz.git_churn.services.MyProjectService
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.FileStatus
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.SmartPsiElementPointer
import kotlin.apply
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.jvm.java
import kotlin.run

class FileNodeDecorator(val project: Project) : ProjectViewNodeDecorator {
    //    private val settings by lazy { project.getService(FsInfoSettings::class.java) }
    private val fileSystemService by lazy { project.getService(MyProjectService::class.java) }
    private val projectRootManager by lazy { ProjectRootManager.getInstance(project) }

    override fun decorate(
        node: ProjectViewNode<*>,
        presentation: PresentationData
    ) {
//        if (!settings.enabled) return
        if (isNodeIgnored(node)) return

        val psiFile = node.value
        if (psiFile is PsiFileSystemItem && !psiFile.isPhysical) return

        val virtualFile = node.virtualFile
            ?: (node.equalityObject as? SmartPsiElementPointer<*>)?.virtualFile
            ?: return

        if (projectRootManager.fileIndex.isExcluded(virtualFile)) return

        val fileNodeDescriptor = fileSystemService.findDescriptor(virtualFile) ?: return

        buildList {
            presentation.locationString?.apply { add(this) }

            add(fileNodeDescriptor.changeCount.toString())
        }.apply {
            if (isNotEmpty()) {
                val joinToString = joinToString(" | ")
                presentation.locationString = joinToString
            }
        }
    }

    private fun isNodeIgnored(node: ProjectViewNode<*>) = node.run {
        fileStatus == FileStatus.IGNORED && parent?.fileStatus == FileStatus.IGNORED
    }
}
