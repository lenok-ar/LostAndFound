package com.example.data.di

import com.example.data.repository.LostFoundRepository
import com.example.domain.repository.ILostFoundRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindLostFoundRepository(
        repository: LostFoundRepository
    ): ILostFoundRepository
}