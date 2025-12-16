package pt.ipca.lojasocial.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipca.lojasocial.data.remote.FirebaseAuthDataSource
import pt.ipca.lojasocial.data.repository.AuthRepositoryImpl
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Singleton

/**
 * Módulo Hilt que fornece dependências da camada de Data.
 *
 * Configurações:
 * - Firebase Auth como Singletons
 * - Repository implementations bound às suas interfaces
 * - Data sources configurados com suas dependências
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        auth: FirebaseAuth,
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(auth)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(dataSource)
    }

}