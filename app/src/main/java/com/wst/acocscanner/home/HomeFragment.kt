package com.wst.acocscanner.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.wst.acocscanner.R
import com.wst.acocscanner.databinding.ActivityHomeBinding
import com.wst.acocscanner.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.regHomeBtn.setOnClickListener {view: View ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegistrationFragment("asd"))
        }
        binding.oeHomeBtn.setOnClickListener {view: View ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOtherEventsFragment())
        }
        return binding.root
    }
}