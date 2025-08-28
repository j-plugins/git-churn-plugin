package com.github.xepozz.git_churn

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class StartupListener() : ProjectActivity {
    override suspend fun execute(project: Project) {
        val service = project.getService(GitChurnService::class.java)
        service.refresh()
    }
}