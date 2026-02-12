package it.quartierevivo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.domain.usecase.ObserveSegnalazioniUseCase
import it.quartierevivo.domain.usecase.SeedSegnalazioniUseCase
import it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel

class AppViewModelFactory(
    private val inviaSegnalazioneUseCase: InviaSegnalazioneUseCase,
    private val observeSegnalazioniUseCase: ObserveSegnalazioniUseCase,
    private val seedSegnalazioniUseCase: SeedSegnalazioniUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SegnalazioneViewModel::class.java) -> {
                SegnalazioneViewModel(inviaSegnalazioneUseCase) as T
            }
            modelClass.isAssignableFrom(MappaSegnalazioniViewModel::class.java) -> {
                MappaSegnalazioniViewModel(observeSegnalazioniUseCase, seedSegnalazioniUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
