package pl.wluczak.care_me

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import pl.wluczak.care_me.ui.theme.Care_meTheme
import pl.wluczak.care_me.ui.theme.Thistle
import pl.wluczak.care_me.ui.theme.ThistleDarkTheme

class ThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun defaultTheme_usesConfiguredLightOrDarkPalette() {
        var actualPrimary: Color? = null
        var systemDarkTheme = false

        composeRule.setContent {
            systemDarkTheme = isSystemInDarkTheme()
            Care_meTheme {
                actualPrimary = MaterialTheme.colorScheme.primary
            }
        }

        composeRule.runOnIdle {
            val expectedPrimary = if (systemDarkTheme) {
                ThistleDarkTheme
            } else {
                Thistle
            }
            assertEquals(expectedPrimary, actualPrimary)
        }
    }
}
