package kr.kyunghee.torquefeedback_20

import android.content.Context
import android.os.Bundle
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
import kr.kyunghee.torquefeedback_20.databinding.FragmentTimeToNewtonBinding

class TimeFragment: Fragment() {
    val timeViewModel: TimeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentTimeToNewtonBinding>(inflater, R.layout.fragment_time_to_newton, container, false)
        binding.viewModel = timeViewModel

        binding.stopButton.setOnClickListener() {
            timeViewModel.cancel()
        }

        binding.seekbarProgress.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.seekbar.progress = timeViewModel.progress.value!!.toInt()
                    val progress = timeViewModel.progress.value!!.toInt()
                    binding.viewModel = timeViewModel
                    val message = Message()
                    Log.d("DDDDDD", progress.toString())
                    when (progress) {
                        in 9999 downTo 999 -> {
                            message.message = "#1$progress"}
                        in 1000 downTo 99 -> {
                            message.message = "#10$progress"
                        }
                        in 100 downTo 9 -> {
                            message.message = "#100$progress"
                        }
                        else -> {
                            message.message = "#1000$progress"
                        }
                    }
                    timeViewModel.sendMessage(message)

                    val imm: InputMethodManager =
                        v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    return true
                }
                return false
            }
        })

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (!p2) {
                    timeViewModel.progress.value = p1.toString()
                    binding.viewModel = timeViewModel
                    val message = Message()
                    when (p1) {
                        in 9999 downTo 999 -> {
                            message.message = "#1$p1"
                        }
                        in 1000 downTo 99 -> {
                            message.message = "#10$p1"
                        }
                        in 100 downTo 9 -> {
                            message.message = "#100$p1"
                        }
                        else -> {
                            message.message = "#1000$p1"
                        }
                    }
                    timeViewModel.sendMessage(message)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("DDDD", "start")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    val progress = p0.progress
                    timeViewModel.progress.value = progress.toString()
                    binding.viewModel = timeViewModel
                    val message = Message()
                    Log.d("DDDDDD", progress.toString())
                    when (progress) {
                        in 9999 downTo 999 -> {
                            message.message = "#1$progress"
                        }
                        in 1000 downTo 99 -> {
                            message.message = "#10$progress"
                        }
                        in 100 downTo 9 -> {
                            message.message = "#100$progress"
                        }
                        else -> {
                            message.message = "#1000$progress"
                        }
                    }
                    timeViewModel.sendMessage(message)
                }
            }

        })
        val spinner  = binding.planetsSpinner

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.times_array,
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
                    timeViewModel.step.value = p0.getItemAtPosition(p2).toString().toInt()
                    Log.d("RRRRRRRRR", "${p0.getItemAtPosition(p2)}")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (p0 != null) {
                    timeViewModel.step.value = p0.getItemAtPosition(0).toString().toInt()
                    Log.d("EEEE", "${p0.getItemAtPosition(0)}")
                }
            }

        }
        binding.addButton.setOnClickListener {
            if (timeViewModel.progress.value!!.toInt() > 0){
                timeViewModel.progress.value = (timeViewModel.progress.value!!.toInt() + timeViewModel.step.value!!).toString()
                binding.seekbar.progress = timeViewModel.progress.value!!.toInt()
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeViewModel.receiveMessage.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }
}