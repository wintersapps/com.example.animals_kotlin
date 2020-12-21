package com.example.animals_kotlin.util

import android.app.Activity
import com.android.billingclient.api.*

class BillingAgent(private val activity: Activity, private val callback: BillingCallback) :
    PurchasesUpdatedListener {

    private var billingClient =
        BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build()
    private val productsSKUList = listOf("id_of_managed_product_in_google_console")
    private val productsList = arrayListOf<SkuDetails>()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    getAvailableProducts()
                }
            }

            override fun onBillingServiceDisconnected() {}

        })
    }

    fun onDestroy() {
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        checkProduct(billingResult, purchases)
    }

    fun checkProduct(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        purchases?.let {
            var token: String? = null
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && it.size > 0
            ) {
                token = it[0].purchaseToken
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                val purchasesList = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                    .purchasesList
                purchasesList?.let { list ->
                    if (list.size > 0) {
                        token = list[0].purchaseToken
                    }
                }
            }

            token?.let { str ->
                val params = ConsumeParams.newBuilder()
                    .setPurchaseToken(str)
//                    .setDeveloperPayload("Token consumed")
                    .build()
                billingClient.consumeAsync(params){ billingResult, _ ->
                     if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                         callback.onTokenConsumed()
                     }
                }
            }
        }
    }

    fun getAvailableProducts() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(productsSKUList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productsList.clear()
                    skuDetailsList?.let {
                        productsList.addAll(it)
                    }
                }
            }
        }
    }

    fun purchaseView() {
        if (productsList.size > 0) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(productsList[0]) //productsList[0] 'cause I have one product
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

}