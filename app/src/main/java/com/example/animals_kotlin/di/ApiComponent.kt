package com.example.animals_kotlin.di

import com.example.animals_kotlin.model.AnimalApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: AnimalApiService)
}