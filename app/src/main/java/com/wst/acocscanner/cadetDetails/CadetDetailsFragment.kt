package com.wst.acocscanner.cadetDetails

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.wst.acocscanner.R
import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.commonURL.Common
import com.wst.acocscanner.databinding.FragmentCadetDetailsBinding
import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.Response

class CadetDetailsFragment : Fragment() {
    private lateinit var binding : FragmentCadetDetailsBinding
    private lateinit var cadetDetailsViewModel: CadetDetailsViewModel
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var mService : IMyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cadet_details, container, false)
        cadetDetailsViewModel = ViewModelProvider(this, CadetDetailsViewModelFactory(requireActivity().application)).get(CadetDetailsViewModel::class.java)
        binding.cadetDetailsViewModel = cadetDetailsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        loadingAnimation = binding.loadingAnimation
        binding.contentLayout.visibility = View.GONE
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        val args = CadetDetailsFragmentArgs.fromBundle(requireArguments())
        val barcode = args.barcode

        mService = Common.api

        showCadetDetails(barcode)

        cadetDetailsViewModel.navigateToRegistrationScanner.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(CadetDetailsFragmentDirections.actionCadetDetailsFragmentToRegistrationFragment())
                cadetDetailsViewModel.doneNavigateToRegistrationScanner()
            }
        })


        return binding.root
    }

    private fun showCadetDetails(barcode: String) {
        mService.getCadetDetails(barcode).enqueue(object : retrofit2.Callback<APIResponse>{
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.body()!!.error){
                    Log.d("TAG", "I WAS CALLED 1")
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()

                    showWarningAlertDialog(response)
                }else{
                    Log.d("TAG", "I WAS CALLED 2")
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()
                    binding.contentLayout.visibility = View.VISIBLE
                    binding.cadetDetailsName.text = response.body()!!.cadetDetails!!.cadetName
                    binding.cadetDetailsCadetNumber.text = response.body()!!.cadetDetails!!.cadetNo
                    binding.cadetDetailsCadetIntake.text = response.body()!!.cadetDetails!!.intake.toString()
                    binding.cadetDetailsShirtSize.text = response.body()!!.cadetDetails!!.shirtSize
                    binding.cadetDetailsGuest.text = response.body()!!.cadetDetails!!.totalAttendance.toString()
                    binding.cadetDetailsTitleAccommodation.text = response.body()!!.cadetDetails!!.accommodationDesc

                    mService.getCadetParking(barcode).enqueue(object : retrofit2.Callback<APIResponse>{
                        override fun onResponse(call1: Call<APIResponse>, response1: Response<APIResponse>) {
                            if (response1.body()!!.error){
                                Log.d("TAG", "I WAS CALLED 3")
                                loadingAnimation.visibility = View.GONE
                                loadingAnimation.cancelAnimation()
                                binding.contentLayout.visibility = View.VISIBLE
                                binding.cadetDetailsParkingNumber.text = response1.body()!!.error_msg
                            }else{
                                Log.d("TAG", "I WAS CALLED 4")
                                loadingAnimation.visibility = View.GONE
                                loadingAnimation.cancelAnimation()
                                binding.contentLayout.visibility = View.VISIBLE
                                val parking = response1.body()!!.parkings
                                val parkingString = parking!!.joinToString(separator = " ,") { it.ParkingNo }
                                binding.cadetDetailsParkingNumber.text = parkingString

                            }
                        }
                        override fun onFailure(call1: Call<APIResponse>, t: Throwable) {
                            Log.d("TAG", "I WAS CALLED 5")
                            showErrorAlertDialog(call1, t, barcode)

                        }
                    })
                }
            }
            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Log.d("TAG", "I WAS CALLED 6")
                loadingAnimation.visibility = View.GONE
                loadingAnimation.cancelAnimation()
                showErrorAlertDialog(call, t, barcode)
            }
        })
    }
    private fun showWarningAlertDialog(response: Response<APIResponse>) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_warning_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleWarning) as TextView).text = "Warning"
        (view.findViewById<View>(R.id.textMessageWarning) as TextView).text = "${response.body()!!.error_msg}"
        (view.findViewById<View>(R.id.buttonActionWarning) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconWarning) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionWarning).setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(CadetDetailsFragmentDirections.actionCadetDetailsFragmentToRegistrationFragment())
//            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()
    }

    private fun showErrorAlertDialog(call: Call<APIResponse>, t: Throwable, barcode: String) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_error_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerError) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleError) as TextView).text = "Error"
        (view.findViewById<View>(R.id.textMessageError) as TextView).text = "${t.message}"
        (view.findViewById<View>(R.id.buttonActionError) as Button).text = "Try Again"
        (view.findViewById<View>(R.id.imageIconError) as ImageView).setImageResource(R.drawable.error)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionError).setOnClickListener {
            alertDialog.dismiss()
            showCadetDetails(barcode)
//            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()

    }
}