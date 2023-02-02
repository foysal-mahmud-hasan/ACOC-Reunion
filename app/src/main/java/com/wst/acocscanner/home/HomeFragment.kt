package com.wst.acocscanner.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.wst.acocscanner.R
import com.wst.acocscanner.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(requireActivity().application)).get(HomeViewModel::class.java)
        binding.homeViewModel = homeViewModel

        binding.lifecycleOwner = viewLifecycleOwner


        homeViewModel.navigateToRegistrationScanner.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegistrationFragment())
                homeViewModel.doneNavigateToRegistrationScanner()
            }
        })

        homeViewModel.navigateToOEScanner.observe(viewLifecycleOwner, Observer {
            if(it == true){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOtherEventsFragment())
                homeViewModel.doneNavigateToOEScanner()
            }
        })

        /*binding.regHomeBtn.setOnClickListener {view: View ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegistrationFragment("asd"))
        }
        binding.oeHomeBtn.setOnClickListener {view: View ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOtherEventsFragment())
        }
        return binding.root*/

        return binding.root
    }
}