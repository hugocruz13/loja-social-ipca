package pt.ipca.lojasocial.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipca.lojasocial.data.remote.FirebaseAuthDataSource
import pt.ipca.lojasocial.data.repository.AuthRepositoryImpl
import pt.ipca.lojasocial.data.repository.BeneficiaryRepositoryImpl
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Singleton

/**
 * Módulo Hilt que fornece dependências da camada de Data.
 *
 * Configurações:
 * - Firebase Auth e Firestore como Singletons
 * - Repository implementations bound às suas interfaces
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- FIREBASE INSTANCES ---

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- DATA SOURCES ---

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        auth: FirebaseAuth,
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(auth)
    }

    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideBeneficiaryRepository(
        firestore: FirebaseFirestore
    ): BeneficiaryRepository {
        return BeneficiaryRepositoryImpl(firestore)
    }
}