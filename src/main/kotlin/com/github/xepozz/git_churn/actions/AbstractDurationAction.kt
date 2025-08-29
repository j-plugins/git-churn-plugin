package com.github.xepozz.git_churn.actions

import com.github.xepozz.git_churn.listener.GitChurnListener
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class AbstractDurationAction(action: AnActionEvent) : AbstractToggleAction(action) {
    val properties = listOf(
        settings::duration1Month,
        settings::duration3Months,
        settings::duration6Months,
        settings::duration1Year,
        settings::durationFull,
    )

    override fun setSelected(selected: Boolean) {
        if (isSelected) {
            return
        }

        properties
            .filter { it != option }
            .forEach { it.set(false) }
        option.set(true)

        GitChurnListener.fireSettingsUpdated()
    }
}