package com.github.xepozz.git_churn.actions

import com.intellij.openapi.actionSystem.ToggleOptionAction
import com.intellij.openapi.project.DumbAware

class Duration1MonthAction : DumbAware, ToggleOptionAction({
    object : AbstractDurationAction(it) {
        override val option = settings::duration1Month
    }
})

class Duration3MonthsAction : DumbAware, ToggleOptionAction({
    object : AbstractDurationAction(it) {
        override val option = settings::duration3Months
    }
})

class Duration6MonthsAction : DumbAware, ToggleOptionAction({
    object : AbstractDurationAction(it) {
        override val option = settings::duration6Months
    }
})

class Duration1YearAction : DumbAware, ToggleOptionAction({
    object : AbstractDurationAction(it) {
        override val option = settings::duration1Year
    }
})

class DurationFullAction : DumbAware, ToggleOptionAction({
    object : AbstractDurationAction(it) {
        override val option = settings::durationFull
    }
})
