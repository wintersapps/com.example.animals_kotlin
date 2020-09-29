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
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.util.getProgressDrawable
import com.example.animals_kotlin.util.loadImage
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    var animal: Animal? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            animal = DetailFragmentArgs.fromBundle(it).animal
        }

        context?.let {
            animalImageImageView.loadImage(animal?.imageUrl, getProgressDrawable(it))
        }

        animalNameTextView.text = animal?.name
        animalLocationTextView.text = animal?.location
        animalLifespanTextView.text = animal?.lifeSpan
        animalDietTextView.text = animal?.diet

        animal?.imageUrl?.let {
            setupBackgroundColor(it)
        }
    }

    private fun setupBackgroundColor(url: String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate(){ palette ->
                            val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                            animalLayout.setBackgroundColor(intColor)
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}