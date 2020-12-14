package com.example.animals_kotlin.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animals_kotlin.databinding.FragmentListBinding
import com.example.animals_kotlin.view.adapters.AnimalListAdapter
import com.example.animals_kotlin.viewmodel.ListViewModel

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListViewModel
    private val listAdapter = AnimalListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()
        observeViewModel()

        binding.animalsListRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = listAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener{
            binding.animalsListRecyclerView.visibility = View.GONE
            binding.loadingDataErrorTextView.visibility = View.GONE
            binding.loadingDataProgressBar.visibility = View.VISIBLE
            viewModel.hardRefresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        viewModel.animals.observe(viewLifecycleOwner, { list ->
            list?.let {
                binding.animalsListRecyclerView.visibility = View.VISIBLE
                listAdapter.updateAnimalList(it)
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                binding.loadingDataProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
                if(loading){
                    binding.loadingDataErrorTextView.visibility = View.GONE
                    binding.animalsListRecyclerView.visibility = View.GONE
                }
            }
        })

        viewModel.loadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                binding.loadingDataErrorTextView.visibility = if(isError) View.VISIBLE else View.GONE
            }
        })
    }

}