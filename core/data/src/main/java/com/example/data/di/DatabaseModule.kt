package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.core.AppConstants
import com.example.data.local.ItemDao
import com.example.data.local.LostFoundDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LostFoundDatabase =
        Room.databaseBuilder(
            context,
            LostFoundDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).build()

    @Provides
    fun provideItemDao(database: LostFoundDatabase): ItemDao = database.itemDao()
}
