package com.example.animals_kotlin.view.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.animals_kotlin.databinding.ItemAnimalBinding
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.view.fragments.ListFragment
import com.example.animals_kotlin.view.fragments.ListFragmentDirections
import com.example.animals_kotlin.view.listeners.AnimalClickListener

class AnimalListAdapter(private val animalList: ArrayList<Animal>, private val listFragment: ListFragment):
    RecyclerView.Adapter<AnimalListAdapter.AnimalViewHolder>(), AnimalClickListener {

    fun updateAnimalList(newAnimalList: List<Animal>){
        animalList.clear()
        animalList.addAll(newAnimalList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = ItemAnimalBinding.inflate(LayoutInflater.from(parent.context))
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.view.animal = animalList[position]
        holder.view.listener = this
    }

    override fun getItemCount() = animalList.size

    class AnimalViewHolder(var view: ItemAnimalBinding): RecyclerView.ViewHolder(view.root)

    override fun onClick(view: View) {
        for(animal in animalList){
            if(TextUtils.equals(view.tag.toString(), animal.name)){
                listFragment.onAnimalClick(animal)
            }
        }
    }
}