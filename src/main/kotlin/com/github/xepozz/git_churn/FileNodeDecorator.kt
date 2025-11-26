package com.github.xepozz.git_churn

import com.github.xepozz.git_churn.config.GitChurnConfigSettings

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vcs.FileStatus
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor

class FileNodeDecorator(val project: Project) : ProjectViewNodeDecorator {
    private val settings by lazy { GitChurnConfigSettings.getInstance() }
    private val fileSystemService by lazy { project.getService(GitChurnService::class.java) }
    private val projectRootManager by lazy { ProjectRootManager.getInstance(project) }

    override fun decorate(
        node: ProjectViewNode<*>,
        presentation: PresentationData,
    ) {
        if (!settings.enabled) return
        if (isNodeIgnored(node)) return

        val psiFile = node.value
        if (psiFile is PsiFileSystemItem && !psiFile.isPhysical) return

        val virtualFile = node.virtualFile ?: (node.equalityObject as? SmartPsiElementPointer<*>)?.virtualFile ?: return

        if (projectRootManager.fileIndex.isExcluded(virtualFile)) return

        val fileNodeDescriptor = fileSystemService.findDescriptor(virtualFile) ?: return
        if (fileNodeDescriptor.changeCount == 0) return

        val isDarkThemeActive = !JBColor.isBright()

        val greyColor = Colors.getGreyColor(isDarkThemeActive)
        val redColor = Colors.getRedColor(isDarkThemeActive)

        val parentDescriptor = node.parentDescriptor

        val maxSteps = maxOf(100, fileSystemService.result.maxCount)

        buildList {
            presentation.locationString?.apply { add(this) }
            if (settings.coloring) {
                val backgroundColor = presentation.background
                    ?: (parentDescriptor as? PresentableNodeDescriptor)?.highlightColor
                    ?: greyColor

                val gradientColor = gradientStep(
                    backgroundColor,
                    redColor,
                    fileNodeDescriptor.changeCount,
                    maxSteps,
                )

                presentation.background = gradientColor
            }

            add(GitChurnBundle.message("changes", fileNodeDescriptor.changeCount))
        }.apply {
            if (isNotEmpty()) {
                val joinToString = joinToString(" | ")
                presentation.locationString = joinToString
            }
        }
    }

    fun gradientStep(fromColor: java.awt.Color, toColor: java.awt.Color, step: Int, maxSteps: Int): java.awt.Color {
        val ratio = step.toDouble() / maxSteps.toDouble()
        val clampedRatio = ratio.coerceIn(0.05, 1.0)

        return ColorUtil.mix(fromColor, toColor, clampedRatio)
    }

    private fun isNodeIgnored(node: ProjectViewNode<*>) = node.run {
        fileStatus == FileStatus.IGNORED && parent?.fileStatus == FileStatus.IGNORED
    }
}
