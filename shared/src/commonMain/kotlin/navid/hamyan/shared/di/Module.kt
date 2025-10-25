package navid.hamyan.shared.di

import androidx.room.RoomDatabase
import io.ktor.client.HttpClient
import navid.hamyan.shared.coins.data.remote.impl.KtorCoinsRemoteDataSource
import navid.hamyan.shared.coins.domain.GetCoinDetailsUseCase
import navid.hamyan.shared.coins.domain.GetCoinPriceHistoryUseCase
import navid.hamyan.shared.coins.domain.GetCoinsListUseCase
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.coins.presentation.CoinsListViewModel
import navid.hamyan.shared.core.database.portfolio.PortfolioDatabase
import navid.hamyan.shared.core.database.portfolio.getPortfolioDatabase
import navid.hamyan.shared.core.network.HttpClientFactory
import navid.hamyan.shared.portfolio.data.PortfolioRepositoryImpl
import navid.hamyan.shared.portfolio.domain.PortfolioRepository
import navid.hamyan.shared.portfolio.presentation.PortfolioViewModel
import navid.hamyan.shared.trade.domain.BuyCoinUseCase
import navid.hamyan.shared.trade.domain.SellCoinUseCase
import navid.hamyan.shared.trade.presentation.buy.BuyViewModel
import navid.hamyan.shared.trade.presentation.sell.SellViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(
        sharedModule,
        platformModule,
    )
}

expect val platformModule: Module

val sharedModule = module {

    single<HttpClient> { HttpClientFactory.create(get()) }

    single {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }
    viewModel { PortfolioViewModel(get()) }

    viewModel { CoinsListViewModel(get(), get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)

    singleOf(::BuyCoinUseCase)
    singleOf(::SellCoinUseCase)
    viewModel { (coinId: String) -> BuyViewModel(coinId, get(), get(), get()) }
    viewModel { (coinId: String) -> SellViewModel(coinId, get(), get(), get()) }

}
