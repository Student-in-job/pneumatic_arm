package kr.kyunghee.torquefeedback_20

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
import com.bosphere.verticalslider.VerticalSlider
import kr.kyunghee.torquefeedback_20.databinding.FragmentScaleBinding
import kr.kyunghee.torquefeedback_20.databinding.FragmentTorqueBinding
import kotlin.math.round

class TorqueFragment: Fragment() {
    val torqueViewModel: TorqueViewModel by activityViewModels()
    var torque: Float = 0f
    var max = 8
    var min = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentTorqueBinding>(inflater, R.layout.fragment_torque, container, false)
        binding.viewModel = torqueViewModel

        binding.stopButton.setOnClickListener() {
            torqueViewModel.cancel()
        }

        binding.startButton.setOnClickListener {
            if (torqueViewModel.torque.value!!.toDouble() > 0){
                //binding.torque.text = Editable.Factory.getInstance().newEditable(torqueViewModel.torque.value!!.toString())
                torqueViewModel.torque.value = binding.torque.text!!.toString().toDouble()
                val message = Torque()
                message.message = torqueViewModel.torque.value!!.toFloat()
                Toast.makeText(requireContext(), torqueViewModel.torque.value!!.toString(), Toast.LENGTH_SHORT).show()
                torqueViewModel.sendTorque(message)
            }
        }

        val vs: VerticalSlider = binding.seekbar
        vs.setOnSliderProgressChangeListener {
            var value = (round((max-min) * 10 * it) / 10 + min)
            //var value = 100 * it
            //var b = round(value * 10) / 10

            binding.torque.text = value.toString()
            //(((it * 1000).toInt().div(50) * 100) + 800).toString()
            torque = it
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        torqueViewModel.receiveMessage.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

    }
}