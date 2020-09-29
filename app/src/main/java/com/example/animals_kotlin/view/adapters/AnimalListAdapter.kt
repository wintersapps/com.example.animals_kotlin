package com.example.animals_kotlin.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.animals_kotlin.R
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.util.getProgressDrawable
import com.example.animals_kotlin.util.loadImage
import com.example.animals_kotlin.view.fragments.ListFragmentDirections
import kotlinx.android.synthetic.main.item_animal.view.*

class AnimalListAdapter(private val animalList: ArrayList<Animal>):
    RecyclerView.Adapter<AnimalListAdapter.AnimalViewHolder>() {

    fun updateAnimalList(newAnimalList: List<Animal>){
        animalList.clear()
        animalList.addAll(newAnimalList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.view.animalNameTextView.text = animalList[position].name
        holder.view.animalImage.loadImage(animalList[position].imageUrl, getProgressDrawable(holder.view.context))
        holder.view.animalLayout.setOnClickListener {
            val action = ListFragmentDirections.actionGoToDetails(animalList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount() = animalList.size

    class AnimalViewHolder(var view: View): RecyclerView.ViewHolder(view)
}