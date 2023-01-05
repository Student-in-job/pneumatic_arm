package com.example.torquefeddback

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.torquefeddback.databinding.FragmentNewtonToTimeBinding
import com.example.torquefeddback.databinding.FragmentTimeToNewtonBinding

class NewtonFragment: Fragment() {
    val newtonViewModel: NewtonViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentNewtonToTimeBinding>(inflater, R.layout.fragment_newton_to_time, container, false)
        binding.viewModel = newtonViewModel

        binding.stopButton.setOnClickListener() {
            newtonViewModel.cancel()
        }

        binding.seekbarProgress.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newtonViewModel.progress.value = v!!.text.toString()
                    val imm: InputMethodManager =
                        v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    val message = Force()
                    message.message = newtonViewModel.progress.value!!.toFloat()
                    Toast.makeText(requireContext(), newtonViewModel.progress.value!!, Toast.LENGTH_SHORT).show()
                    newtonViewModel.sendForce(message)
                    return true
                }
                return false
            }
        })


        val spinner  = binding.planetsSpinner

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.newtons_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("SSSSSSSSS", "$p1")
                Log.d("BBBBB", "$p2")
                Log.d("AAAAA", "$p3")
                if (p0 != null) {
                    newtonViewModel.step.value = p0.getItemAtPosition(p2).toString().toDouble()
                    Log.d("RRRRRRRRR", "${p0.getItemAtPosition(p2)}")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (p0 != null) {
                    newtonViewModel.step.value = p0.getItemAtPosition(0).toString().toDouble()
                    Log.d("EEEE", "${p0.getItemAtPosition(0)}")
                }
            }

        }
        binding.addButton.setOnClickListener {
            if (newtonViewModel.progress.value!!.toDouble() > 0){
                newtonViewModel.progress.value = (newtonViewModel.progress.value!!.toDouble() + newtonViewModel.step.value!!).toString()
                binding.seekbarProgress.text = Editable.Factory.getInstance().newEditable(newtonViewModel.progress.value!!)
                val message = Force()
                message.message = newtonViewModel.progress.value!!.toFloat()
                Toast.makeText(requireContext(), newtonViewModel.progress.value!!, Toast.LENGTH_SHORT).show()
                newtonViewModel.sendForce(message)
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newtonViewModel.receiveMessage.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }
}