package pt.ipca.lojasocial.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipca.lojasocial.data.remote.FirebaseAuthDataSource
import pt.ipca.lojasocial.data.repository.AuthRepositoryImpl
import pt.ipca.lojasocial.data.repository.BeneficiaryRepositoryImpl
import pt.ipca.lojasocial.data.repository.CampaignRepositoryImpl
import pt.ipca.lojasocial.data.repository.RequestRepositoryImpl
import pt.ipca.lojasocial.data.repository.StorageRepositoryImpl
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
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
    fun provideFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance("loja-social-ipca-db")
    }
    // --- DATA SOURCES ---

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        auth: FirebaseAuth,
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository {
        return StorageRepositoryImpl(storage)
    }

    @Provides
    @Singleton
    fun provideCampaignRepository(firestore: FirebaseFirestore): CampaignRepository {
        return CampaignRepositoryImpl(firestore)
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

    @Provides
    @Singleton
    fun provideRequestRepository(firestore: FirebaseFirestore): RequestRepository {
        return RequestRepositoryImpl(firestore)
    }
}