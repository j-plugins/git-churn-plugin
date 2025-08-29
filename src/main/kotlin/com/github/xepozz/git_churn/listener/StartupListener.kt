package com.github.xepozz.git_churn.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class StartupListener() : ProjectActivity {
    override suspend fun execute(project: Project) {
        GitChurnListener.fireSettingsUpdated()
    }
}