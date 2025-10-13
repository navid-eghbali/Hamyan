package navid.hamyan.shared.trade.presentation.common.component

import androidx.compose.ui.text.input.OffsetMapping

class CurrencyOffsetMapping(originalText: String, formattedText: String) : OffsetMapping {
    private val originalLength = originalText.length
    private val indices = findDigitIndices(originalText, formattedText)

    override fun originalToTransformed(offset: Int): Int = if (offset >= originalLength) {
        indices.last() + 1
    } else {
        indices[offset]
    }

    override fun transformedToOriginal(offset: Int): Int =
        indices.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength

    private fun findDigitIndices(first: String, second: String): List<Int> {
        val digitIndices = mutableListOf<Int>()
        var currentIndex = 0
        for (digit in first) {
            val index = second.indexOf(digit, currentIndex)
            if (index != -1) {
                digitIndices.add(index)
                currentIndex = index + 1
            } else {
                return emptyList()
            }
        }
        return digitIndices
    }
}
