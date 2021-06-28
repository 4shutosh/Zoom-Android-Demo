package com.poc.zoom.start

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.poc.zoom.R
import com.poc.zoom.databinding.FragmentStartBinding
import com.poc.zoom.utils.ActivityNavigationDispatchers
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    private lateinit var binding: FragmentStartBinding

    @Inject
    lateinit var navigationDispatchers: ActivityNavigationDispatchers

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStartBinding.bind(view)
        setUpView()
    }

    private fun setUpView() {
        binding.joinMeeting.setOnClickListener {
            navigationDispatchers.emit {
                it.navigate(
                    StartFragmentDirections.actionStartFragmentToMainFragment(
                        meetingNumber = binding.meetingNumber.text.toString(),
                        userName = binding.meetingName.text.toString()
                    )
                )
            }
        }
    }
}