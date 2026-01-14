package com.fittrack.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module providing Firebase-related dependencies.
 * Currently disabled - Firebase requires google-services.json setup.
 * Uncomment the providers when Firebase is configured.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Firebase is disabled for now - add google-services.json to enable
    
    // @Provides
    // @Singleton
    // fun provideFirebaseAuth(): FirebaseAuth {
    //     return FirebaseAuth.getInstance()
    // }

    // @Provides
    // @Singleton
    // fun provideFirebaseFirestore(): FirebaseFirestore {
    //     return FirebaseFirestore.getInstance()
    // }
}
