package com.github.xepozz.git_churn.notification

import com.github.xepozz.git_churn.GitChurnIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project

object NotificationUtil {
    fun sendNotification(
        project: Project,
        title: String,
        message: String,
        actions: Collection<AnAction> = emptyList(),
    ) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Git Churn")
            .createNotification(
                title,
                message,
                NotificationType.WARNING,
            )
        notification.isImportant = true
        notification.icon = GitChurnIcons.GIT_CHURN

        notification.addActions(actions)

        notification.notify(project)
    }
}