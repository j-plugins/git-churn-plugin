package com.github.xepozz.git_churn.actions

import com.intellij.openapi.actionSystem.ToggleOptionAction
import com.intellij.openapi.project.DumbAware

class ToggleEnabledAction : DumbAware, ToggleOptionAction({
    object : AbstractToggleAction(it) {
        override val option = settings::enabled
        override fun isEnabled() = true
    }
})
