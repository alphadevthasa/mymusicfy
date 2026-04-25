package com.musicwave.app.di

import com.musicwave.app.data.MusicRepositoryImpl
import com.musicwave.app.core.domain.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: MusicRepositoryImpl): MusicRepository
}
