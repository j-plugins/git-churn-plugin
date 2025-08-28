package com.github.xepozz.git_churn

import com.intellij.ui.ColorUtil

object Colors {
    val lightGrey = ColorUtil.fromHex("#6E6E6E")
    val darkGrey = ColorUtil.fromHex("#AFB1B3")

    val lightRed = ColorUtil.fromHex("#DB5860")
    val darkRed = ColorUtil.fromHex("#C75450")

    fun getGreyColor(isDarkThemeActive: Boolean) = if (isDarkThemeActive) darkGrey else lightGrey
    fun getRedColor(isDarkThemeActive: Boolean) = if (isDarkThemeActive) darkRed else lightRed
}