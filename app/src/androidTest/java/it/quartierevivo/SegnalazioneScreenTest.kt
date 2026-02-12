package it.quartierevivo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.repository.SegnalazioneRepository
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

class SegnalazioneScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun criticalFieldsAndActionsAreVisible_usingResourceStrings() {
        val activity = composeTestRule.activity
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(FakeSegnalazioneRepository()))

        composeTestRule.setContent {
            SegnalazioneScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(activity.getString(R.string.title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.description)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.category)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.select_photo)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.get_location)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.send)).assertIsDisplayed()
    }

    @Test
    fun sendWithMissingRequiredFieldsShowsValidationMessage() {
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(FakeSegnalazioneRepository()))

        composeTestRule.setContent {
            SegnalazioneScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.send)).performClick()

        composeTestRule.onNodeWithText("Titolo e categoria sono obbligatori").assertExists()
    }

    @Test
    fun sendWithValidDataShowsSuccessSnackbar() {
        val activity = composeTestRule.activity
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(FakeSegnalazioneRepository()))
        viewModel.onCategoriaChange(activity.getString(R.string.report_category_safety))

        composeTestRule.setContent {
            SegnalazioneScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(activity.getString(R.string.title)).performTextInput("Lampione")
        composeTestRule.onNodeWithText(activity.getString(R.string.send)).performClick()

        composeTestRule.onNodeWithText("Segnalazione inviata").assertExists()
    }

    private class FakeSegnalazioneRepository : SegnalazioneRepository {
        override fun observeSegnalazioni(): Flow<List<Segnalazione>> = emptyFlow()

        override suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit> = Result.success(Unit)

        override suspend fun seedSegnalazioniIfEmpty() = Unit
    }
}
