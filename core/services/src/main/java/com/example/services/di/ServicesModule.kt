package com.example.services.di

import com.example.services.analytics.AnalyticsService
import com.example.services.analytics.AppMetricaAnalyticsService
import com.example.services.auth.EncryptedTokenStorage
import com.example.services.auth.AuthService
import com.example.services.auth.SdkAuthService
import com.example.services.auth.TokenStorage
import com.example.services.map.MapService
import com.example.services.map.YandexMapService
import com.example.services.profile.FirebaseUserProfileService
import com.example.services.profile.UserProfileService
import com.example.services.remoteconfig.FirebaseRemoteConfigService
import com.example.services.remoteconfig.RemoteConfigService
import com.example.services.crash.CombinedCrashReporter
import com.example.services.crash.CrashReporter
import com.example.services.ai.AiAssistantService
import com.example.services.ai.GigaChatAssistantService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsService(implementation: AppMetricaAnalyticsService): AnalyticsService

    @Binds
    @Singleton
    abstract fun bindTokenStorage(implementation: EncryptedTokenStorage): TokenStorage

    @Binds
    @Singleton
    abstract fun bindAuthService(implementation: SdkAuthService): AuthService

    @Binds
    @Singleton
    abstract fun bindMapService(implementation: YandexMapService): MapService

    @Binds
    @Singleton
    abstract fun bindRemoteConfigService(implementation: FirebaseRemoteConfigService): RemoteConfigService

    @Binds
    @Singleton
    abstract fun bindUserProfileService(implementation: FirebaseUserProfileService): UserProfileService

    @Binds
    @Singleton
    abstract fun bindCrashReporter(implementation: CombinedCrashReporter): CrashReporter

    @Binds
    @Singleton
    abstract fun bindAiAssistantService(implementation: GigaChatAssistantService): AiAssistantService
}
