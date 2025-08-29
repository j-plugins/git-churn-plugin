package com.github.xepozz.git_churn.config

import com.github.xepozz.git_churn.listener.GitChurnListener
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class GitChurnConfigurable : Configurable {
    private val settings = GitChurnConfigSettings.getInstance()
    private lateinit var settingsComponent: GitChurnConfigSettingsComponent

    override fun getDisplayName(): String = "Git Churn"

    override fun createComponent(): JComponent {
        settingsComponent = GitChurnConfigSettingsComponent()
        return settingsComponent.panel
    }

    override fun isModified(): Boolean {
        val component = settingsComponent
        val state = settings.state
        return settingsComponent.panel.isModified() ||
                component.getExcludePatterns() != state.excludePatterns
    }

    override fun apply() {
        settingsComponent.panel.apply()

        val component = settingsComponent
        val state = settings.state
        state.excludePatterns = component.getExcludePatterns().toMutableList()

        GitChurnListener.fireSettingsUpdated()
    }

    override fun reset() {
        settingsComponent.loadSettings(settings.state)
        settingsComponent.panel.reset()
    }
}