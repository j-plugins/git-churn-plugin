package com.github.xepozz.git_churn.config

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.ColumnInfo
import javax.swing.DefaultCellEditor
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class PatternColumnInfo : ColumnInfo<StringColumn, String>("Pattern") {
    override fun valueOf(item: StringColumn): String = item.value

    override fun setValue(item: StringColumn, value: String) {
        item.value = value
    }

    override fun isCellEditable(item: StringColumn) = true

    override fun getRenderer(item: StringColumn): TableCellRenderer {
        return object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable,
                value: Any?,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ) = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).apply {
                text = value?.toString() ?: ""
                toolTipText = "Double-click to edit pattern"
            }
        }
    }

    override fun getEditor(item: StringColumn): TableCellEditor {
        return object : DefaultCellEditor(JBTextField()) {
            override fun getTableCellEditorComponent(
                table: JTable,
                value: Any?,
                isSelected: Boolean,
                row: Int,
                column: Int
            ): JComponent {
                val textField = component as JBTextField
                textField.text = value?.toString() ?: ""
                return textField
            }

            override fun getCellEditorValue(): Any {
                return (component as JBTextField).text
            }
        }
    }
}