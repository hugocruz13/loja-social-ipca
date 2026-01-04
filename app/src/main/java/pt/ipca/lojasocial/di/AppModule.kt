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
import pt.ipca.lojasocial.data.repository.LogRepositoryImpl
import pt.ipca.lojasocial.data.repository.ProductRepositoryImpl
import pt.ipca.lojasocial.data.repository.CommunicationRepositoryImpl
import pt.ipca.lojasocial.data.repository.CampaignRepositoryImpl
import pt.ipca.lojasocial.data.repository.RequestRepositoryImpl
import pt.ipca.lojasocial.data.repository.SchoolYearRepositoryImpl
import pt.ipca.lojasocial.data.repository.StaffRepositoryImpl
import pt.ipca.lojasocial.data.repository.StockRepositoryImpl
import pt.ipca.lojasocial.data.repository.StorageRepositoryImpl
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import pt.ipca.lojasocial.domain.repository.LogRepository
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
        store: FirebaseFirestore
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(auth, store)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideCampaignRepository(firestore: FirebaseFirestore): CampaignRepository {
        return CampaignRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(dataSource, firestore)
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

    @Provides
    @Singleton
    fun provideSchoolYearRepository(firestore: FirebaseFirestore): SchoolYearRepository {
        return SchoolYearRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideStaffRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): StaffRepository = StaffRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideLogRepository(firestore: FirebaseFirestore): LogRepository {
        return LogRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideProductRepository(firestore: FirebaseFirestore): ProductRepository {
        return ProductRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun provideStockRepository(firestore: FirebaseFirestore): StockRepository {
        return StockRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun provideCommunicationRepository(firestore: FirebaseFirestore): CommunicationRepository {
        return CommunicationRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository {
        return StorageRepositoryImpl(storage)
    }
}