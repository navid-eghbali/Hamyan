package navid.hamyan.shared.portfolio.data

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.EmptyResult
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.onError
import navid.hamyan.shared.core.domain.onSuccess
import navid.hamyan.shared.portfolio.data.local.PortfolioDao
import navid.hamyan.shared.portfolio.data.local.UserBalanceDao
import navid.hamyan.shared.portfolio.data.local.UserBalanceEntity
import navid.hamyan.shared.portfolio.data.mapper.toPortfolioCoinEntity
import navid.hamyan.shared.portfolio.data.mapper.toPortfolioCoinModel
import navid.hamyan.shared.portfolio.domain.PortfolioCoinModel
import navid.hamyan.shared.portfolio.domain.PortfolioRepository

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRemoteDataSource: CoinsRemoteDataSource,
) : PortfolioRepository {
    override suspend fun initializeBalance() {
        val currentBalance = userBalanceDao.getCashBalance()
        if (currentBalance == null) {
            userBalanceDao.insertBalance(
                UserBalanceEntity(cashBalance = 10000.0)
            )
        }
    }

    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> =
        portfolioDao.getAllOwnedCoins()
            .flatMapLatest { entities ->
                if (entities.isEmpty()) {
                    flow {
                        emit(Result.Success(emptyList()))
                    }
                } else {
                    flow {
                        coinsRemoteDataSource.getListOfCoins()
                            .onError { emit(Result.Error(it)) }
                            .onSuccess { responseDto ->
                                val portfolioCoins = entities.mapNotNull { entity ->
                                    responseDto.data.coins.find { it.uuid == entity.coinId }?.let { coin ->
                                        entity.toPortfolioCoinModel(coin.price)
                                    }
                                }
                                emit(Result.Success(portfolioCoins))
                            }
                    }
                }
            }.catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { return Result.Error(it) }
            .onSuccess { responseDto ->
                return portfolioDao.getCoinById(coinId)?.let {
                    Result.Success(it.toPortfolioCoinModel(responseDto.data.coin.price))
                } ?: Result.Success(null)
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> = try {
        portfolioDao.insert(portfolioCoin.toPortfolioCoinEntity())
        Result.Success(Unit)
    } catch (_: SQLiteException) {
        Result.Error(DataError.Local.DISK_FULL)
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deletePortfolioItem(coinId)
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> =
        portfolioDao.getAllOwnedCoins()
            .flatMapLatest { entities ->
                if (entities.isEmpty()) {
                    flow { emit(Result.Success(0.0)) }
                } else {
                    flow {
                        coinsRemoteDataSource.getListOfCoins()
                            .onError { emit(Result.Error(it)) }
                            .onSuccess { responseDto ->
                                val totalValue = entities.sumOf { ownedCoin ->
                                    val coinPrice = responseDto.data.coins.find { it.uuid == ownedCoin.coinId }?.price ?: 0.0
                                    ownedCoin.amountOwned * coinPrice
                                }
                                emit(Result.Success(totalValue))
                            }
                    }
                }
            }.catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }

    override fun cashBalanceFlow(): Flow<Double> = flow {
        emit(userBalanceDao.getCashBalance() ?: 10000.0)
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> = combine(
        cashBalanceFlow(),
        calculateTotalPortfolioValue(),
    ) { cashBalance, totalPortfolioValue ->
        when (totalPortfolioValue) {
            is Result.Success -> Result.Success(cashBalance + totalPortfolioValue.data)
            is Result.Error -> Result.Error(totalPortfolioValue.error)
        }
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}
