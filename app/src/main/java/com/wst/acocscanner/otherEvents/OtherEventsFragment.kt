package com.wst.acocscanner.otherEvents

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.wst.acocscanner.R
import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.commonURL.Common
import com.wst.acocscanner.databinding.FragmentOtherEventsBinding
import com.wst.acocscanner.registration.RegistrationFragmentDirections
import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherEventsFragment : Fragment() {

    private lateinit var binding: FragmentOtherEventsBinding
    private lateinit var mService : IMyApi
    private lateinit var oeViewModel: OtherEventsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_events, container, false)
        oeViewModel = ViewModelProvider(this, OtherEventsViewModelFactory(requireActivity().application)).get(OtherEventsViewModel::class.java)
        binding.oeViewModel = oeViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        mService = Common.api



        mService.loadEvents("submit").enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.body()!!.error) {
                    showWarningAlertDialog(response)
                } else {
                    val events = response.body()!!.eventDetId
                    if (events != null && events.isNotEmpty()) {
                        val options = events.map { it.subEventTitle}
                        val spinner = binding.spinner
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                    }
                }

            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                // Handle failure of the API call
            }
        })


        oeViewModel.navigateToHomeFragment.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(OtherEventsFragmentDirections.actionOtherEventsFragmentToHomeFragment())
                oeViewModel.doneNavigateToHome()
            }
        })

        return binding.root
    }

    private fun showWarningAlertDialog(response: Response<APIResponse>) {

    }

    private fun showSuccessAlertDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_success_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleSuccess) as TextView).text = "Success"
        (view.findViewById<View>(R.id.textMessageSuccess) as TextView).text = "This Qr Code Successfully Registered For "
        (view.findViewById<View>(R.id.buttonActionSuccess) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconSuccess) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionSuccess).setOnClickListener {
            alertDialog.dismiss()
//            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()
    }

}
