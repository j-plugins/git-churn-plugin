package com.github.xepozz.git_churn.startup

import com.github.xepozz.git_churn.services.MyProjectService
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class MyProjectActivity() : ProjectActivity {

    override suspend fun execute(project: Project) {
        val service = project.getService(MyProjectService::class.java)
//        println("call service")
        service.callGitLog()
    }
}