package com.example.animals_kotlin.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animals_kotlin.BuildConfig
import com.example.animals_kotlin.R
import com.example.animals_kotlin.databinding.FragmentListBinding
import com.example.animals_kotlin.model.Animal
import com.example.animals_kotlin.view.adapters.AnimalListAdapter
import com.example.animals_kotlin.viewmodel.ListViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListViewModel
    private val listAdapter = AnimalListAdapter(arrayListOf(), this)
    private lateinit var rewardedAd: RewardedAd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rewardedAd = RewardedAd(binding.root.context, getString(R.string.rewarded_ad_id))

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

        if(BuildConfig.FLAVOR == "free") {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }else{
            binding.adViewLayout.visibility = View.GONE
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

    fun onAnimalClick(animal: Animal){
        if(BuildConfig.FLAVOR == "free") {
            binding.loadingDataProgressBar.visibility = View.VISIBLE
            binding.animalsListRecyclerView.visibility = View.GONE
            showRewardedAd(animal)
        }else{
            goToAnimalDetails(animal)
        }
    }

    private fun showRewardedAd(animal: Animal){
        val adCallback = object: RewardedAdCallback() {
            override fun onRewardedAdClosed() {
                showList()
            }
            override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                goToAnimalDetails(animal)
            }
            override fun onRewardedAdFailedToShow(adError: AdError) {
                goToAnimalDetails(animal)
            }
        }
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                rewardedAd.show(activity, adCallback)
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                goToAnimalDetails(animal)
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    private fun showList(){
        binding.loadingDataProgressBar.visibility = View.GONE
        binding.animalsListRecyclerView.visibility = View.VISIBLE
    }

    private fun goToAnimalDetails(animal: Animal){
        val action = ListFragmentDirections.actionGoToDetails(animal)
        Navigation.findNavController(binding.root).navigate(action)
    }

}