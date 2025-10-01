package navid.hamyan.shared.core.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import navid.hamyan.shared.portfolio.data.local.PortfolioCoinEntity
import navid.hamyan.shared.portfolio.data.local.PortfolioDao
import navid.hamyan.shared.portfolio.data.local.UserBalanceDao
import navid.hamyan.shared.portfolio.data.local.UserBalanceEntity

@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
}
