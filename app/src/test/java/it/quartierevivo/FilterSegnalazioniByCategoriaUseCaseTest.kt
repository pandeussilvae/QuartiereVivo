package it.quartierevivo

import it.quartierevivo.domain.usecase.FilterSegnalazioniByCategoriaUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterSegnalazioniByCategoriaUseCaseTest {

    private val useCase = FilterSegnalazioniByCategoriaUseCase()

    @Test
    fun `returns all reports when category filter is null`() {
        val reports = listOf(
            Segnalazione("1", "Lampione rotto", 45.0, 9.0, categoria = "Manutenzione"),
            Segnalazione("2", "Rifiuti", 45.1, 9.1, categoria = "Altro"),
        )

        val result = useCase(reports, null)

        assertEquals(reports, result)
    }

    @Test
    fun `returns only reports matching category`() {
        val reports = listOf(
            Segnalazione("1", "Lampione rotto", 45.0, 9.0, categoria = "Manutenzione"),
            Segnalazione("2", "Rifiuti", 45.1, 9.1, categoria = "Altro"),
            Segnalazione("3", "Buio in strada", 45.2, 9.2, categoria = "Manutenzione"),
        )

        val result = useCase(reports, "Manutenzione")

        assertEquals(2, result.size)
        assertTrue(result.all { it.categoria == "Manutenzione" })
    }
}
