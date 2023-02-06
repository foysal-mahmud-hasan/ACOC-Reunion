package com.wst.acocscanner.registration

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.*
import com.wst.acocscanner.R
import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.commonURL.Common
import com.wst.acocscanner.databinding.FragmentRegistrationBinding
import com.wst.acocscanner.home.HomeFragmentDirections
import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistrationFragment : Fragment() {


    private lateinit var binding : FragmentRegistrationBinding
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var mService : IMyApi

    private val CAMERA_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        prefs = requireActivity().getSharedPreferences("Id", Context.MODE_PRIVATE)
//        val userId = prefs.getInt("Id", 0)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)
        registrationViewModel = ViewModelProvider(this, RegistrationViewModelFactory(requireActivity().application)).get(RegistrationViewModel::class.java)
        binding.registrationViewModel = registrationViewModel

        binding.lifecycleOwner = viewLifecycleOwner

        scannerView = binding.scannerView
        loadingAnimation = binding.loadingAnimation
        sharedPref = requireActivity().getSharedPreferences("com.wst.acocscanner", Context.MODE_PRIVATE)

        mService = Common.api

        checkCameraPermission()


        /*// Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)

        binding.regBackBtn.setOnClickListener {view: View ->
            view.findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToCadetDetailsFragment())
        }*/

        registrationViewModel.navigateToHomeFragment.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToHomeFragment())
                registrationViewModel.doneNavigateToHome()
            }
        })


        return binding.root
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireContext() as Activity,arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            startScanning(scannerView)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning(scannerView)
            } else {
                AlertDialog.Builder(context)
                    .setTitle("Permission Required")
                    .setMessage("Camera permission is required to use the code scanner.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun startScanning(scannerView: CodeScannerView) {
        codeScanner = CodeScanner(requireContext(), scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread{
                binding.contentLayout.visibility = View.GONE
                loadingAnimation.visibility = View.VISIBLE
                loadingAnimation.playAnimation()
                registerBarcode(it.text.toString())
//                Toast.makeText(requireContext(), "Scan Result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread{
                Toast.makeText(requireContext(), "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    private fun registerBarcode(barcode: String) {
        val userId = sharedPref.getInt("Id", 0)
        Log.d("USER ID", userId.toString())

        mService.validateQrCode(barcode)
            .enqueue(object  : retrofit2.Callback<APIResponse>{
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    if (response.body()!!.error) {
                        loadingAnimation.visibility = View.GONE
                        loadingAnimation.cancelAnimation()
                        binding.contentLayout.visibility = View.VISIBLE
                        showWarningAlertDialog(response)
                    }else{
                        Log.d("TAG", "REGISTRATION ID ${response!!.body()!!.registrationdetails!!.registrationId}")
                        mService.checkCoupon(barcode, 1, response!!.body()!!.registrationdetails!!.registrationId, userId).enqueue(object : retrofit2.Callback<APIResponse>{
                            override fun onResponse(call: Call<APIResponse>, response1: Response<APIResponse>) {
                                if (response1.body()!!.error){
                                    loadingAnimation.visibility = View.GONE
                                    loadingAnimation.cancelAnimation()
                                    binding.contentLayout.visibility = View.VISIBLE
                                    showWarningAlertDialog(response1)
                                }else if(response1!!.body()!!.event == null){
                                    loadingAnimation.visibility = View.GONE
                                    loadingAnimation.cancelAnimation()
                                    binding.contentLayout.visibility = View.VISIBLE
                                    if (response!!.body()!!.registrationdetails!!.isCadet == 1){
                                        Toast.makeText(requireContext(), "Scanned Successfully", Toast.LENGTH_LONG).show()
                                        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToCadetDetailsFragment(barcode))
                                    } else{
                                        showSuccessAlertDialog(response1)
                                    }
                                } else{
                                    loadingAnimation.visibility = View.GONE
                                    loadingAnimation.cancelAnimation()
                                    binding.contentLayout.visibility = View.VISIBLE
                                    showWarningAlertDialogAR(response1)
                                }
                            }
                            override fun onFailure(call1: Call<APIResponse>, t1: Throwable) {
                                loadingAnimation.visibility = View.GONE
                                loadingAnimation.cancelAnimation()
                                binding.contentLayout.visibility = View.VISIBLE
                                Log.d("TAG", "I WAS CALLED NESTED API CALL")
                                showErrorAlertDialog(call1, t1)
                            }
                        })
                    }
                }
                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()
                    binding.contentLayout.visibility = View.VISIBLE
                    Log.d("TAG", "I WAS CALLED MAIN API CALL")
                    showErrorAlertDialog(call, t)
                }
            })


    }

    private fun showErrorAlertDialog(call: Call<APIResponse>, t: Throwable) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_error_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerError) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleError) as TextView).text = "Error"
        (view.findViewById<View>(R.id.textMessageError) as TextView).text = "${t.message}"
        (view.findViewById<View>(R.id.buttonActionError) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconError) as ImageView).setImageResource(R.drawable.error)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionError).setOnClickListener {
            alertDialog.dismiss()
            codeScanner.startPreview()

//            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()

    }

    private fun showSuccessAlertDialog(response: Response<APIResponse>) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_success_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleSuccess) as TextView).text = "Success"
        (view.findViewById<View>(R.id.textMessageSuccess) as TextView).text = "Scanned Successfully"
        (view.findViewById<View>(R.id.buttonActionSuccess) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconSuccess) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionSuccess).setOnClickListener {
            codeScanner.startPreview()
            alertDialog.dismiss()
//            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()

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
            codeScanner.startPreview()
//            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()

    }
    private fun showWarningAlertDialogAR(response: Response<APIResponse>) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_warning_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleWarning) as TextView).text = "Warning"
        (view.findViewById<View>(R.id.textMessageWarning) as TextView).text = "This coupon already scanned for Registration event"
        (view.findViewById<View>(R.id.buttonActionWarning) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconWarning) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionWarning).setOnClickListener {
            alertDialog.dismiss()
            codeScanner.startPreview()
//            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()

    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
        super.onPause()
    }




}