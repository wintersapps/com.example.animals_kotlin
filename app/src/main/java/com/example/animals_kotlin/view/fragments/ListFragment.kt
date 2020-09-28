package com.example.animals_kotlin.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animals_kotlin.R
import com.example.animals_kotlin.view.adapters.AnimalListAdapter
import com.example.animals_kotlin.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val listAdapter = AnimalListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()
        observeViewModel()

        animalsListRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = listAdapter
        }

        swipeRefreshLayout.setOnRefreshListener{
            animalsListRecyclerView.visibility = View.GONE
            loadingDataErrorTextView.visibility = View.GONE
            loadingDataProgressBar.visibility = View.VISIBLE
            viewModel.hardRefresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeViewModel(){
        viewModel.animals.observe(viewLifecycleOwner, { list ->
            list?.let {
                animalsListRecyclerView.visibility = View.VISIBLE
                listAdapter.updateAnimalList(it)
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                loadingDataProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
                if(loading){
                    loadingDataErrorTextView.visibility = View.GONE
                    animalsListRecyclerView.visibility = View.GONE
                }
            }
        })

        viewModel.loadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                loadingDataErrorTextView.visibility = if(isError) View.VISIBLE else View.GONE
            }
        })
    }

}