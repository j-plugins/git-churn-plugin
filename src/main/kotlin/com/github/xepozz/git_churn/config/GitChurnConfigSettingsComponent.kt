package com.github.xepozz.git_churn.config

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import java.awt.Dimension
import javax.swing.JCheckBox

class GitChurnConfigSettingsComponent() {
    private val settings = GitChurnConfigSettings.getInstance()

    private val excludePatternsModel = ListTableModel<StringColumn>(
        arrayOf(PatternColumnInfo()),
        emptyList()
    )
    private val excludePatternsTable = TableView(excludePatternsModel).apply {
        preferredScrollableViewportSize = Dimension(400, 200)
        setShowGrid(true)
        isStriped = true
    }
    private val maxHistoryDaysField: JBTextField = JBTextField()
    private val tableToolbar = ToolbarDecorator.createDecorator(excludePatternsTable)
        .setAddAction { addPattern() }
        .createPanel()

    lateinit var enabledCheckbox: JCheckBox
    val panel = panel {
        row {
            enabledCheckbox = checkBox("Enabled")
                .onApply { settings.enabled = enabledCheckbox.isSelected }
                .bindSelected(settings::enabled)
                .component
        }
        row {
            checkBox("Colors")
                .comment("Highlight files in Project View based on churn")
                .bindSelected(settings::coloring)
        }
        separator()
        row {
            cell(tableToolbar)
                .align(Align.FILL)
                .label("Exclude patterns:", LabelPosition.TOP)
                .comment("Exclude files from churn highlighting. Wildcards are supported")
                .bind(
                    {
                        val map = excludePatternsModel.items.map { it.value }
                        map.toMutableList()
                    },
                    { pane, value ->
                        excludePatternsModel.items = value.map { StringColumn(it) }
                    },
                    settings::excludePatterns.toMutableProperty()
                )
        }.enabled(enabledCheckbox.isSelected)
    }

    fun getExcludePatterns(): List<String> = excludePatternsModel.items.map { it.value }

    fun loadSettings(state: GitChurnConfigSettings) {
        maxHistoryDaysField.text = state.maxHistoryDays.toString()
        excludePatternsModel.items = state.excludePatterns
            .map { StringColumn(it) }
            .toMutableList()
    }

    private fun addPattern() {
        val pattern = Messages.showInputDialog(
            panel,
            "Enter exclude pattern:",
            "Add Exclude Pattern",
            Messages.getQuestionIcon(),
            "",
            null
        )

        if (!pattern.isNullOrBlank()) {
//            println("add pattern $pattern")
            excludePatternsModel.addRow(StringColumn(pattern.trim()))
        }
    }

    private fun editPattern(event: AnActionEvent) {
        println("edit pattern ${event}")
        val pattern = Messages.showInputDialog(
            panel,
            "Enter exclude pattern:",
            "Add Exclude Pattern",
            Messages.getQuestionIcon(),
            "",
            null
        )

        if (!pattern.isNullOrBlank()) {
//            println("add pattern $pattern")
//            excludePatternsModel.addRow(StringColumn(pattern.trim()))
        }
    }
}