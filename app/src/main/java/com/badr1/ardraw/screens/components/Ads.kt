package com.badr1.ardraw.screens.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.badr1.ardraw.AD_BANNER
import com.badr1.ardraw.AD_INTERSTITIAL
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = AD_BANNER
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}


class InterstitialAdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd(
        adUnitId: String = AD_INTERSTITIAL, // Test ID
        onAdLoaded: () -> Unit = {},
        onAdFailedToLoad: (String) -> Unit = {}
    ) {
        if (isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    onAdLoaded()

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            loadAd(adUnitId) // Auto-reload for next time
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    onAdFailedToLoad(error.message)
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onAdDismissed: () -> Unit = {},  // Called after ad closes
        onAdFailed: () -> Unit = {}      // Called if ad fails to show
    ) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd() // Reload for next time
                    onAdDismissed() // Navigate here
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadAd()
                    onAdFailed() // Navigate here too
                }

                override fun onAdShowedFullScreenContent() {
                    // Ad is showing
                }
            }
            interstitialAd?.show(activity)
        } else {
            if (!isLoading) {
                loadAd(
                    onAdLoaded = {
                        // Ad loaded successfully, show it
                        showAd(activity, onAdDismissed, onAdFailed)
                    },
                    onAdFailedToLoad = {
                        // Failed to load, navigate anyway
                        onAdFailed()
                    }
                )
            } else {
                // Already loading, just navigate
                onAdFailed()
            }
        }
    }

    fun isAdReady(): Boolean = interstitialAd != null
}
