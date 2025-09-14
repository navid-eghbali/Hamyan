package navid.hamyan.shared.di

import io.ktor.client.HttpClient
import navid.hamyan.shared.coins.data.remote.impl.KtorCoinsRemoteDataSource
import navid.hamyan.shared.coins.domain.GetCoinDetailsUseCase
import navid.hamyan.shared.coins.domain.GetCoinsListUseCase
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.coins.presentation.CoinsListViewModel
import navid.hamyan.shared.core.network.HttpClientFactory
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

    viewModel { CoinsListViewModel(get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)
}
