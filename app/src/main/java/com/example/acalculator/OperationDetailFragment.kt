package com.example.acalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.acalculator.databinding.FragmentOperationDetailBinding

private const val ARG_OPERATION = "operation"

class OperationDetailFragment : Fragment() {
    private var operation: OperationUi? = null
    private lateinit var binding: FragmentOperationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { operation = it.getParcelable(ARG_OPERATION) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        operation?.let{ (requireActivity() as AppCompatActivity).supportActionBar?.title = it.expression }
        val view = inflater.inflate(R.layout.fragment_operation_detail, container, false)
        binding = FragmentOperationDetailBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        operation?.let {
            binding.textExpression.text = it.expression
            binding.textResult.text = it.result
            binding.textDatetime.text = it.getDate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(operation: OperationUi) = OperationDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_OPERATION, operation)
            }
        }
    }
}