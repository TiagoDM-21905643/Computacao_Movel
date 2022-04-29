package com.example.acalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acalculator.databinding.FragmentHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_OPERATIONS = "operations"

class HistoryFragment : Fragment() {
    private val model = Calculator
    private val adapter = HistoryAdapter(::onOperationClick, ::onOperationLongClick)
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.history)
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        binding = FragmentHistoryBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.rvHistoric.layoutManager = LinearLayoutManager(context)
        binding.rvHistoric.adapter = adapter

        model.getHistory { updateHistory(it) }
    }

    private fun onOperationClick(operation: OperationUi) {
        NavigationManager.goToOperationDetailFragment(parentFragmentManager, operation)
    }
    private fun onOperationLongClick(operation: OperationUi): Boolean {
        Toast.makeText(context, operation.getDate(), Toast.LENGTH_LONG).show()
        return false
    }
    private fun updateHistory(operations: List<Operation>) {
        val history = operations.map { OperationUi(it.expression, it.result, it.timestamp) }
        CoroutineScope(Dispatchers.Main).launch {
            showHistory(history.isNotEmpty())
            adapter.updateItems(history)
        }
    }

    private fun showHistory(show: Boolean) {
        if (show) {
            binding.rvHistoric.visibility = View.VISIBLE
            binding.textNoHistoryAvailable.visibility = View.GONE
        } else {
            binding.rvHistoric.visibility = View.GONE
            binding.textNoHistoryAvailable.visibility = View.VISIBLE
        }
    }
}