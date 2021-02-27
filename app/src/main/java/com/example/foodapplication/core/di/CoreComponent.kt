package com.example.foodapplication.core.di

import android.content.Context
import com.example.foodapplication.core.domain.repository.IFoodRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface CoreComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context):CoreComponent
    }

    fun provideRepository():IFoodRepository
}