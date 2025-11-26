package com.github.xepozz.git_churn.actions

import com.github.xepozz.git_churn.config.GitChurnConfigurable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

class OpenSettingsAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, GitChurnConfigurable::class.java)
    }
}