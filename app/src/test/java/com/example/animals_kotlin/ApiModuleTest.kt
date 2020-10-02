package com.example.animals_kotlin

import com.example.animals_kotlin.di.ApiModule
import com.example.animals_kotlin.model.AnimalApiService

class ApiModuleTest(val mockService: AnimalApiService): ApiModule() {

    override fun provideAnimalApiService(): AnimalApiService {
        return mockService
    }
}