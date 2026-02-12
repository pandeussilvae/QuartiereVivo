package it.quartierevivo

import android.net.Uri
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.repository.SegnalazioneRepository
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.presentation.common.UiState
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SegnalazioneViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updates all user inputs correctly`() {
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(FakeSegnalazioneRepository()))
        val photo = Uri.parse("content://quartierevivo/photo.jpg")

        viewModel.onTitoloChange("Titolo test")
        viewModel.onDescrizioneChange("Descrizione test")
        viewModel.onCategoriaChange("Sicurezza")
        viewModel.onPosizioneChange("Lat:45.0,Lng:9.0")
        viewModel.onFotoChange(photo)

        assertEquals("Titolo test", viewModel.titolo)
        assertEquals("Descrizione test", viewModel.descrizione)
        assertEquals("Sicurezza", viewModel.categoria)
        assertEquals("Lat:45.0,Lng:9.0", viewModel.posizione)
        assertEquals(photo, viewModel.fotoUri)
    }

    @Test
    fun `submit emits Loading then Success and clears form`() = runTest {
        val repository = FakeSegnalazioneRepository(
            sendResult = Result.success(Unit),
            sendDelayMillis = 100,
        )
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(repository))
        val states = mutableListOf<UiState<Unit>>()

        viewModel.onTitoloChange("Lampione guasto")
        viewModel.onDescrizioneChange("Via Roma")
        viewModel.onCategoriaChange("Manutenzione")
        viewModel.onPosizioneChange("Lat:45.4642, Lng:9.1900")

        val collectJob = launch {
            viewModel.uiState.take(3).toList(states)
        }

        viewModel.inviaSegnalazione()
        advanceUntilIdle()
        collectJob.join()

        assertEquals(listOf(UiState.Empty, UiState.Loading, UiState.Success(Unit)), states)
        assertEquals("", viewModel.titolo)
        assertEquals("", viewModel.descrizione)
        assertEquals("", viewModel.categoria)
        assertNull(viewModel.fotoUri)
        assertNull(viewModel.posizione)
        assertEquals(1, repository.sentInputs.size)
    }

    @Test
    fun `submit emits Error when required fields are missing`() = runTest {
        val viewModel = SegnalazioneViewModel(InviaSegnalazioneUseCase(FakeSegnalazioneRepository()))

        viewModel.inviaSegnalazione()
        advanceUntilIdle()

        assertEquals(UiState.Error("Titolo e categoria sono obbligatori"), viewModel.uiState.value)
    }

    @Test
    fun `submit emits Error when use case fails then reset returns Empty`() = runTest {
        val viewModel = SegnalazioneViewModel(
            InviaSegnalazioneUseCase(
                FakeSegnalazioneRepository(sendResult = Result.failure(IllegalStateException("Errore test")))
            )
        )

        viewModel.onTitoloChange("Titolo")
        viewModel.onCategoriaChange("Altro")
        viewModel.inviaSegnalazione()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
        assertEquals("Errore test", (viewModel.uiState.value as UiState.Error).message)

        viewModel.resetUiState()
        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    private class FakeSegnalazioneRepository(
        private val sendResult: Result<Unit> = Result.success(Unit),
        private val sendDelayMillis: Long = 0,
    ) : SegnalazioneRepository {

        val sentInputs = mutableListOf<SegnalazioneInput>()

        override fun observeSegnalazioni(): Flow<List<Segnalazione>> = emptyFlow()

        override suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit> {
            if (sendDelayMillis > 0) {
                delay(sendDelayMillis)
            }
            sentInputs += input
            return sendResult
        }

        override suspend fun seedSegnalazioniIfEmpty() = Unit
    }
}
