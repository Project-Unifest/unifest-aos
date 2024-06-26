package com.unifest.android.core.data.di

import com.unifest.android.core.data.repository.BoothRepository
import com.unifest.android.core.data.repository.BoothRepositoryImpl
import com.unifest.android.core.data.repository.FestivalRepository
import com.unifest.android.core.data.repository.FestivalRepositoryImpl
import com.unifest.android.core.data.repository.LikedBoothRepositoryImpl
import com.unifest.android.core.data.repository.LikedBoothRepository
import com.unifest.android.core.data.repository.LikedFestivalRepository
import com.unifest.android.core.data.repository.LikedFestivalRepositoryImpl
import com.unifest.android.core.data.repository.OnboardingRepository
import com.unifest.android.core.data.repository.OnboardingRepositoryImpl
import com.unifest.android.core.data.repository.RemoteConfigRepository
import com.unifest.android.core.data.repository.RemoteConfigRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFestivalRepository(festivalRepositoryImpl: FestivalRepositoryImpl): FestivalRepository

    @Binds
    @Singleton
    abstract fun bindBoothRepository(boothRepositoryImpl: BoothRepositoryImpl): BoothRepository

    @Binds
    @Singleton
    abstract fun bindLikedFestivalRepository(likedFestivalRepositoryImpl: LikedFestivalRepositoryImpl): LikedFestivalRepository

    @Binds
    @Singleton
    abstract fun bindLikedBoothRepository(likedBoothRepositoryImpl: LikedBoothRepositoryImpl): LikedBoothRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(onboardingRepositoryImpl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindRemoteConfigRepository(remoteConfigRepositoryImpl: RemoteConfigRepositoryImpl): RemoteConfigRepository
}
