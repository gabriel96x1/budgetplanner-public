package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.bytesdrawer.budgetplanner.MainActivity

class PurchasesManager(private val context: Context) {

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private var productDetails: List<ProductDetails>?  = null

    fun getPurchaseOptions() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySubs()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.w("Billing Result", "ServiceDisconnected")
            }
        })
    }

    private fun querySubs() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("sub_bp01")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build())
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            productDetails = productDetailsList.toList()
            Log.d("Billing Result", billingResult.responseCode.toString())
            Log.d("Billing Product Details", productDetails.toString())
            // check billingResult
            launchBillingFlow()
        }
    }

    private fun launchBillingFlow() {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails!!.first())
                // for a list of offers that are available to the user
                .setOfferToken(productDetails!!.first().subscriptionOfferDetails!!.first().offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(context as MainActivity, billingFlowParams)

    }

}