package com.example.animals_kotlin.view.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.animals_kotlin.R
import com.example.animals_kotlin.databinding.FragmentDetailBinding
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.model.AnimalPalette
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class DetailFragment : Fragment() {

    var animal: Animal? = null
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showInterstitialAd()

        arguments?.let {
            animal = DetailFragmentArgs.fromBundle(it).animal
        }

        animal?.imageUrl?.let {
            setupBackgroundColor(it)
        }

        binding.animal = animal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBackgroundColor(url: String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                            binding.palette = AnimalPalette(intColor)
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun showInterstitialAd(){
        interstitialAd = InterstitialAd(binding.root.context)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                interstitialAd.show()
            }
        }
    }
}