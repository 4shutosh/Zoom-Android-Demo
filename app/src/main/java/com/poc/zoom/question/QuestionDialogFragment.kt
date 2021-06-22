package com.poc.zoom.question

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.poc.zoom.R
import com.poc.zoom.databinding.LayoutDialogQuestionBinding
import com.poc.zoom.utils.executeAfter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionDialogFragment : DialogFragment(R.layout.layout_dialog_question) {

    private lateinit var binding: LayoutDialogQuestionBinding

    private val viewModel: QuestionDialogViewModel by viewModels()

    private val args: QuestionDialogFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutDialogQuestionBinding.bind(view)

        setUpView()
        initLiveData()
    }

    private fun setUpView() {
        binding.executeAfter {
            question = args.questionData
        }

        binding.option1.setOnClickListener {
            viewModel.sendUserAnswer(args.questionData.id, args.userName, args.questionData.option1)
        }
        binding.option2.setOnClickListener {
            viewModel.sendUserAnswer(args.questionData.id, args.userName, args.questionData.option2)
        }
        binding.option3.setOnClickListener {
            viewModel.sendUserAnswer(args.questionData.id, args.userName, args.questionData.option3)
        }
        binding.option4.setOnClickListener {
            viewModel.sendUserAnswer(args.questionData.id, args.userName, args.questionData.option4)
        }
    }

    private fun initLiveData() {
        viewModel.dismissTrigger.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}