package com.example.booksy.di

import android.content.Context
import com.example.booksy.AppDatabase
import com.example.booksy.LibrosDao
import com.example.booksy.LibrosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideLibrosDao(database: AppDatabase): LibrosDao {
        return database.librosDao()
    }

    @Provides
    @Singleton
    fun provideLibrosRepository(dao: LibrosDao): LibrosRepository {
        return LibrosRepository(dao)
    }
}
