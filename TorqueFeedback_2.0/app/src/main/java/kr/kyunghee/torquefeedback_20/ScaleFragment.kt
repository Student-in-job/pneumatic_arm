package kr.kyunghee.torquefeedback_20

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bosphere.verticalslider.VerticalSlider

import android.R
import androidx.fragment.app.viewModels
import com.bosphere.verticalslider.VerticalSlider.OnProgressChangeListener
import kr.kyunghee.torquefeedback_20.databinding.FragmentScaleBinding
import kotlin.properties.Delegates


class ScaleFragment : Fragment() {
    val scaleViewModel by viewModels<ScaleViewModel>()
    lateinit var binding: FragmentScaleBinding
    var progress: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentScaleBinding.inflate(LayoutInflater.from(inflater.context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vs: VerticalSlider = binding.seekbar
        vs.setOnSliderProgressChangeListener {
            binding.scaleSize.text =
                (((it * 1000).toInt().div(50) * 100) + 800).toString()
            progress = it
        }

        binding.startButton.setOnClickListener {
            val message = Message()
            var progressInt = progress.toInt()
            progressInt = ((progress * 1000).toInt().div(50) * 100) + 800
            when (progressInt) {
                in 9999 downTo 999 -> {
                    message.message = "#1$progressInt"
                }
                in 1000 downTo 99 -> {
                    message.message = "#10$progressInt"
                }
                in 100 downTo 9 -> {
                    message.message = "#100$progressInt"
                }
                else -> {
                    message.message = "#1000$progressInt"
                }
            }

            scaleViewModel.sendMessage(message)
        }

        binding.relaxButton.setOnClickListener {
            scaleViewModel.cancel()
        }

    }
}