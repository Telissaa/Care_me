package pl.wluczak.care_me.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.wluczak.care_me.data.local.AppDatabase

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            })
            .build()
    }

    single { get<AppDatabase>().activityDao() }
    single { get<AppDatabase>().stepDao() }
    single { get<AppDatabase>().reminderDao() }
}
