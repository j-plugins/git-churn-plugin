package com.github.xepozz.git_churn.listener

import com.github.xepozz.git_churn.GitChurnService
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GitChurnUpdater(
    val project: Project,
    val coroutineScope: CoroutineScope,
) : GitChurnListener {
    override fun onSettingsUpdated() {
        val service = project.getService(GitChurnService::class.java)
        coroutineScope.launch {
            service.refresh()

            ProjectView
                .getInstance(project)
                .currentProjectViewPane
                ?.updateFromRoot(true)
        }
    }
}