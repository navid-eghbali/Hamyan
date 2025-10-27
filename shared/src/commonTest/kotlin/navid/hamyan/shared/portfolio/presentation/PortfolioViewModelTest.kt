package navid.hamyan.shared.portfolio.presentation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.util.formatFiat
import navid.hamyan.shared.core.util.toUiText
import navid.hamyan.shared.portfolio.data.PortfolioRepositoryFake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val repository = PortfolioRepositoryFake()
    private lateinit var viewModel: PortfolioViewModel

    @BeforeTest
    fun setup() {
        viewModel = PortfolioViewModel(
            portfolioRepository = repository,
            coroutineDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `State and portfolio coins are properly combined`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.coins.isEmpty())

            val portfolioCoin = PortfolioRepositoryFake.PORTFOLIO_COIN
            repository.savePortfolioCoin(portfolioCoin)

            awaitItem()
            val updatedState = awaitItem()
            assertTrue(updatedState.coins.isNotEmpty())
            assertFalse(updatedState.isLoading)
            assertEquals(PortfolioRepositoryFake.PORTFOLIO_COIN.coin.id, updatedState.coins.first().id)
        }
    }

    @Test
    fun `Portfolio value updates when a coin is added`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(initialState.portfolioValue, formatFiat(10000.0))

            val portfolioCoin = PortfolioRepositoryFake.PORTFOLIO_COIN.copy(
                ownedAmountInUnit = 50.0,
                ownedAmountInFiat = 1000.0,
            )
            repository.savePortfolioCoin(portfolioCoin)
            val updatedState = awaitItem()
            assertEquals(formatFiat(11000.0), updatedState.portfolioValue)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Loading state and error message update on failure`() = runTest {
        repository.simulateError()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(DataError.Remote.SERVER.toUiText(), state.error)
        }
    }

}
