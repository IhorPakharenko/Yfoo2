package com.isao.yfoo2.core.utils

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.printToString

fun ComposeContentTestRule.printSemantics(
    tag: String = "Semantics",
    useUnmergedTree: Boolean = false
) {
    val semantics = onAllNodes(isRoot(), useUnmergedTree).printToString(Int.MAX_VALUE)
    println("${tag}:\n${semantics}")
}