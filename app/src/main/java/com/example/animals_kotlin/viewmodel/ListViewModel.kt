package com.example.animals_kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.animals_kotlin.di.AppModule
import com.example.animals_kotlin.di.CONTEXT_APP
import com.example.animals_kotlin.di.DaggerViewModelComponent
import com.example.animals_kotlin.di.TypeOfContext
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.model.AnimalApiService
import com.example.animals_kotlin.model.ApiKey
import com.example.animals_kotlin.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel(application: Application): AndroidViewModel(application){

    constructor(application: Application, test: Boolean = true): this(application){
        injected = true
    }

    val animals by lazy { MutableLiveData<List<Animal>>() }
    val loadError by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var animalApiService: AnimalApiService

    @Inject
    @TypeOfContext(CONTEXT_APP)
    lateinit var prefs: SharedPreferencesHelper
    private var invalidApiKey = false
    private var injected = false

    fun inject() {
        if(!injected){
            DaggerViewModelComponent.builder()
                .appModule(AppModule(getApplication()))
                .build()
                .inject(this)
        }
    }

    fun refresh(){
        inject()
        loading.value = true
        invalidApiKey = false
        val key = prefs.getApiKey()
        if(key.isNullOrEmpty()){
            getKey()
        }else{
            getAnimals(key)
        }
    }

    fun hardRefresh(){
        inject()
        loading.value = true
        getKey()
    }

    private fun getKey(){
        compositeDisposable.add(
            animalApiService.getApiKey()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<ApiKey>(){
                    override fun onSuccess(apiKey: ApiKey) {
                        if(apiKey.key.isNullOrEmpty()){
                            loading.value = false
                            loadError.value = true
                        }else{
                            prefs.saveApiKey(apiKey.key)
                            getAnimals(apiKey.key)
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        loadError.value = true
                    }

                })
        )
    }

    private fun getAnimals(key: String){
        compositeDisposable.add(
            animalApiService.getAnimals(key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<Animal>>(){
                    override fun onSuccess(list: List<Animal>) {
                        loadError.value = false
                        animals.value = list
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        if(!invalidApiKey){
                            invalidApiKey = true
                            getKey()
                        }else{
                            e.printStackTrace()
                            loading.value = false
                            animals.value = null
                            loadError.value = true
                        }
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}