package com.wst.acocscanner.otherEvents

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.wst.acocscanner.R
import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.commonURL.Common
import com.wst.acocscanner.databinding.FragmentOtherEventsBinding
import com.wst.acocscanner.model.EventList
import com.wst.acocscanner.registration.RegistrationFragmentDirections
import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherEventsFragment : Fragment() {

    private lateinit var binding: FragmentOtherEventsBinding
    private lateinit var mService : IMyApi
    private lateinit var oeViewModel: OtherEventsViewModel
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView
    private lateinit var sharedPref: SharedPreferences
    private var selectedId : Int = 0
    private var selectedTitle : String = ""

    private val CAMERA_REQUEST_CODE = 1
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

        binding.contentLayout.visibility = View.GONE
        loadingAnimation = binding.loadingAnimation
        scannerView = binding.scannerViewOE
        scannerView.visibility = View.GONE
        sharedPref = requireActivity().getSharedPreferences("com.wst.acocscanner", Context.MODE_PRIVATE)


        mService = Common.api

        codeScanner = CodeScanner(requireContext(), scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isFlashEnabled = false


        val spinner = binding.spinner
        spinner.visibility = View.VISIBLE
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        var events = emptyList<EventList>()

        checkCameraPermission()

        mService.loadEvents("submit").enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.body()!!.error) {
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()
                    binding.contentLayout.visibility = View.VISIBLE
                    showWarningAlertDialog(response)
                } else {
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()
                    binding.contentLayout.visibility = View.VISIBLE
                    Log.d("TAG", "I WAS CALLED")
                    events = response.body()!!.eventList!!
                    val simplified = ArrayList<EventList>(events.size)
                    events.forEach { item -> simplified.add(item) }
                    val objectToAdd = EventList().apply {
                        eventId = 0
                        subEventTitle = "Please Select A Event"
                    }

                    if (events != null && events.isNotEmpty()) {
                        /*val parking = response.body()!!.eventList
                        val parkingString = parking!!.joinToString(separator = " ,") { it.subEventTitle }
                        Log.d("Data source", events.map { it.subEventTitle }.toString())
                        Log.d("Data source", {parkingString}.toString())*/
                        adapter.clear()
                        simplified.add(0, objectToAdd)
                        adapter.addAll(simplified.map { it.subEventTitle })
                        adapter.notifyDataSetChanged()
                        spinner.setSelection(0)
                    }
                }
            }
            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                showErrorAlertDialog(call, t)
            }
        })
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    Toast.makeText(requireContext(), "Please select a valid event", Toast.LENGTH_SHORT).show()
                } else {
                    scannerView.visibility = View.VISIBLE
                    selectedId = events[position-1].eventId
                    selectedTitle = events[position - 1].subEventTitle
                    Log.d("Selected ID", selectedId.toString())
                    startScanning(scannerView)
                    codeScanner.startPreview()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }



        oeViewModel.navigateToHomeFragment.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(OtherEventsFragmentDirections.actionOtherEventsFragmentToHomeFragment())
                oeViewModel.doneNavigateToHome()
            }
        })

        return binding.root
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireContext() as Activity,arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {

        }
    }

    private fun startScanning(scannerView: CodeScannerView) {

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


        //FIRST CALL TO CHECK COUPON IS VALID AND GOOD FOR ENTRY

        mService.validateQrCodeOE(barcode).enqueue(object  : retrofit2.Callback<APIResponse>{
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    if (response.body()!!.error) {
                        loadingAnimation.visibility = View.GONE
                        loadingAnimation.cancelAnimation()
                        binding.contentLayout.visibility = View.VISIBLE
                        showWarningAlertDialogAN(response)
                    }else{
                        Log.d("TAG", "REGISTRATION ID $selectedId")
                        val regId = response.body()!!.registrationIdForOE!!.registrationId

                        //SECOND CALL TO CHECK COUPON HAS ENTERED IN REGISTRATION
                        mService.checkCouponFirst(barcode, 1).enqueue(object : retrofit2.Callback<APIResponse>{
                            override fun onResponse(call1: Call<APIResponse>, response1: Response<APIResponse>) {
                                if (!response1.body()!!.error == false){
                                    loadingAnimation.visibility = View.GONE
                                    loadingAnimation.cancelAnimation()
                                    binding.contentLayout.visibility = View.VISIBLE
                                    showWarningAlertDialog(response1)
                                }else{
                                    loadingAnimation.visibility = View.GONE
                                    loadingAnimation.cancelAnimation()
                                    binding.contentLayout.visibility = View.VISIBLE

                                    //LAST CALL TO REGISTER THE BARCODE

                                    mService.checkCoupon(barcode, selectedId, regId, userId).enqueue(object : Callback<APIResponse> {
                                        override fun onResponse(call2: Call<APIResponse>, response2: Response<APIResponse>) {
                                            if (response2.body()!!.error) {
                                                loadingAnimation.visibility = View.GONE
                                                loadingAnimation.cancelAnimation()
                                                binding.contentLayout.visibility = View.VISIBLE
                                                showWarningAlertDialog(response2)
                                            } else if (response2!!.body()!!.event == null) {
                                                loadingAnimation.visibility = View.GONE
                                                loadingAnimation.cancelAnimation()
                                                binding.contentLayout.visibility = View.VISIBLE
                                                showSuccessAlertDialog(response2)
                                            } else {
                                                loadingAnimation.visibility = View.GONE
                                                loadingAnimation.cancelAnimation()
                                                binding.contentLayout.visibility = View.VISIBLE
                                                showWarningAlertDialogAR(response2)
                                            }
                                        }

                                        override fun onFailure(call2: Call<APIResponse>, t2: Throwable) {
                                            loadingAnimation.visibility = View.GONE
                                            loadingAnimation.cancelAnimation()
                                            binding.contentLayout.visibility = View.VISIBLE
                                            Log.d("TAG", "I WAS CALLED NESTED API CALL")
                                            showErrorAlertDialog(call2, t2)
                                        }
                                    })
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
                        // this is for other events barcode check and registration
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

    private fun showWarningAlertDialogAN(response: Response<APIResponse>) {
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
        (view.findViewById<View>(R.id.textMessageWarning) as TextView).text = "This coupon already scanned for $selectedTitle event"
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
    private fun showWarningAlertDialog(response: Response<APIResponse>) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_warning_dialog,
            (view?.findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout)
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleWarning) as TextView).text = "Warning"
        (view.findViewById<View>(R.id.textMessageWarning) as TextView).text = "Not scanned in Registration Event"
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

    private fun showSuccessAlertDialog(response: Response<APIResponse>) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_success_dialog, null)
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER
        linearLayout.addView(view)
        builder.setView(linearLayout)
        (view.findViewById<View>(R.id.textTitleSuccess) as TextView).text = "Success"
        (view.findViewById<View>(R.id.textMessageSuccess) as TextView).text = "Scanned Successfully"
        (view.findViewById<View>(R.id.buttonActionSuccess) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconSuccess) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionSuccess).setOnClickListener {
            alertDialog.dismiss()
            codeScanner.startPreview()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()
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
            findNavController().navigate(OtherEventsFragmentDirections.actionOtherEventsFragmentToHomeFragment())

//            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
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
