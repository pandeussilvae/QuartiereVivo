package it.quartierevivo

import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.repository.SegnalazioneRepository
import it.quartierevivo.domain.usecase.ObserveSegnalazioniUseCase
import it.quartierevivo.domain.usecase.SeedSegnalazioniUseCase
import it.quartierevivo.presentation.common.UiState
import it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MappaSegnalazioniViewModelTest {

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
    fun `uiState goes from Loading to Success and filter works`() = runTest {
        val repository = FakeMappaRepository()
        val viewModel = MappaSegnalazioniViewModel(
            observeSegnalazioniUseCase = ObserveSegnalazioniUseCase(repository),
            seedSegnalazioniUseCase = SeedSegnalazioniUseCase(repository),
        )

        assertEquals(UiState.Loading, viewModel.uiState.value)

        repository.emit(
            listOf(
                Segnalazione("1", "Lampione", latitudine = 45.0, longitudine = 9.0, categoria = "Manutenzione"),
                Segnalazione("2", "Vetri rotti", latitudine = 45.1, longitudine = 9.1, categoria = "Sicurezza"),
                Segnalazione("3", "Cassonetti", latitudine = 45.2, longitudine = 9.2, categoria = "Altro"),
            )
        )
        advanceUntilIdle()

        val success = viewModel.uiState.value as UiState.Success
        assertEquals(3, success.data.size)
        assertEquals(1, repository.seedCalls)

        viewModel.setCategoriaFiltro("Sicurezza")
        advanceUntilIdle()

        val filtered = viewModel.uiState.value as UiState.Success
        assertEquals(1, filtered.data.size)
        assertEquals("Vetri rotti", filtered.data.first().titolo)
    }

    @Test
    fun `uiState is Empty when repository emits empty list`() = runTest {
        val repository = FakeMappaRepository()
        val viewModel = MappaSegnalazioniViewModel(
            observeSegnalazioniUseCase = ObserveSegnalazioniUseCase(repository),
            seedSegnalazioniUseCase = SeedSegnalazioniUseCase(repository),
        )

        repository.emit(emptyList())
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Error when observe fails`() = runTest {
        val repository = ErrorMappaRepository()
        val viewModel = MappaSegnalazioniViewModel(
            observeSegnalazioniUseCase = ObserveSegnalazioniUseCase(repository),
            seedSegnalazioniUseCase = SeedSegnalazioniUseCase(repository),
        )

        advanceUntilIdle()
        val errorState = viewModel.uiState.first { it is UiState.Error } as UiState.Error

        assertTrue(errorState.message.contains("boom"))
    }

    private class FakeMappaRepository : SegnalazioneRepository {
        private val updates = MutableSharedFlow<List<Segnalazione>>(replay = 1)
        var seedCalls: Int = 0
            private set

        suspend fun emit(segnalazioni: List<Segnalazione>) {
            updates.emit(segnalazioni)
        }

        override fun observeSegnalazioni(): Flow<List<Segnalazione>> = updates

        override suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit> = Result.success(Unit)

        override suspend fun seedSegnalazioniIfEmpty() {
            seedCalls++
        }
    }

    private class ErrorMappaRepository : SegnalazioneRepository {
        override fun observeSegnalazioni(): Flow<List<Segnalazione>> = flow {
            throw IllegalStateException("boom")
        }

        override suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit> = Result.success(Unit)

        override suspend fun seedSegnalazioniIfEmpty() = Unit
    }
}
