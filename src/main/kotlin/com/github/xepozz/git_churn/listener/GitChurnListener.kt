package com.github.xepozz.git_churn.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic

interface GitChurnListener {
    fun onSettingsUpdated() {}

    companion object {
        @JvmStatic
        val TOPIC: Topic<GitChurnListener> = Topic.create(
            "GitChurn.SettingsUpdated.Topic",
            GitChurnListener::class.java
        )

        fun fireSettingsUpdated() {
            ApplicationManager.getApplication().messageBus
                .syncPublisher(TOPIC)
                .onSettingsUpdated()
        }
    }
}