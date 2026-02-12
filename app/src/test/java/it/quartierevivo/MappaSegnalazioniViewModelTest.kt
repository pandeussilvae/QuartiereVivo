package it.quartierevivo

import org.junit.Assert.assertEquals
import org.junit.Test

class MappaSegnalazioniViewModelTest {

    private val segnalazioni = listOf(
        Segnalazione("1", "Lampione", 45.0, 9.0, categoria = "Manutenzione"),
        Segnalazione("2", "Vetri rotti", 45.1, 9.1, categoria = "Sicurezza"),
        Segnalazione("3", "Cassonetti", 45.2, 9.2, categoria = "Altro"),
    )

    @Test
    fun `updates reports and returns filtered list by category`() {
        val viewModel = MappaSegnalazioniViewModel()

        viewModel.aggiornaSegnalazioni(segnalazioni)
        viewModel.setCategoriaFiltro("Sicurezza")

        assertEquals(1, viewModel.segnalazioniFiltrate.value.size)
        assertEquals("Vetri rotti", viewModel.segnalazioniFiltrate.value.first().titolo)
    }

    @Test
    fun `returns full list when category filter is reset`() {
        val viewModel = MappaSegnalazioniViewModel()

        viewModel.aggiornaSegnalazioni(segnalazioni)
        viewModel.setCategoriaFiltro("Sicurezza")
        viewModel.setCategoriaFiltro(null)

        assertEquals(3, viewModel.segnalazioniFiltrate.value.size)
    }
}
