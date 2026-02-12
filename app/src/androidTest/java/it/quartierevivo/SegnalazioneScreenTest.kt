package it.quartierevivo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.hasSetTextAction
import org.junit.Rule
import org.junit.Test

class SegnalazioneScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun criticalFieldsAndActionsAreVisible() {
        composeTestRule.setContent {
            SegnalazioneScreen(viewModel = SegnalazioneViewModel())
        }

        composeTestRule.onNodeWithText("Titolo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descrizione").assertIsDisplayed()
        composeTestRule.onNodeWithText("Categoria").assertIsDisplayed()
        composeTestRule.onNodeWithText("Seleziona foto").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ottieni posizione").assertIsDisplayed()
        composeTestRule.onNodeWithText("Invia").assertIsDisplayed()
    }

    @Test
    fun sendActionShowsConfirmationSnackbar() {
        composeTestRule.setContent {
            SegnalazioneScreen(viewModel = SegnalazioneViewModel())
        }

        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Lampione guasto")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Via Roma")
        composeTestRule.onNodeWithText("Invia").performClick()

        composeTestRule.onNodeWithText("Segnalazione inviata").assertExists()
    }
}
