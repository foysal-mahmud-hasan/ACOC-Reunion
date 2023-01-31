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
import androidx.navigation.findNavController
import com.wst.acocscanner.R
import com.wst.acocscanner.databinding.FragmentOtherEventsBinding
import com.wst.acocscanner.registration.RegistrationFragmentDirections
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherEventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherEventsFragment : Fragment() {

    private lateinit var binding: FragmentOtherEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_events, container, false)
        val options = arrayOf("Option 1", "Option 2", "Option 3")

        val spinner = binding.spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        binding.OEBackBtn.setOnClickListener {view: View ->
            showSuccessAlertDialog()
        }

        return binding.root
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
