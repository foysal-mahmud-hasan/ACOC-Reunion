package com.wst.acocscanner.registration

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.wst.acocscanner.R
import com.wst.acocscanner.databinding.FragmentRegistrationBinding
import com.wst.acocscanner.home.HomeFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistrationFragment : Fragment() {


    private lateinit var binding : FragmentRegistrationBinding
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView

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
                registerBarcode(it.text.toString())
                Toast.makeText(requireContext(), "Scan Result: ${it.text}", Toast.LENGTH_LONG).show()
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
       TODO()


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