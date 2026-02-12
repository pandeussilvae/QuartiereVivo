package it.quartierevivo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SegnalazioneViewModelTest {

    @Test
    fun `updates all user inputs correctly`() {
        val viewModel = SegnalazioneViewModel()

        viewModel.onTitoloChange("Titolo test")
        viewModel.onDescrizioneChange("Descrizione test")
        viewModel.onCategoriaChange("Sicurezza")
        viewModel.onPosizioneChange("Lat:45,Lng:9")

        assertEquals("Titolo test", viewModel.titolo)
        assertEquals("Descrizione test", viewModel.descrizione)
        assertEquals("Sicurezza", viewModel.categoria)
        assertEquals("Lat:45,Lng:9", viewModel.posizione)
        assertNull(viewModel.fotoUri)
    }

    @Test
    fun `confirm flag is enabled and reset on send flow`() {
        val viewModel = SegnalazioneViewModel()

        viewModel.inviaSegnalazione()
        assertTrue(viewModel.invioConfermato)

        viewModel.resetConferma()
        assertFalse(viewModel.invioConfermato)
    }
}
