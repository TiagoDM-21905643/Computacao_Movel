package com.example.acalculator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acalculator.databinding.FragmentHistoryBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request

private const val ARG_OPERATIONS = "operations"

class HistoryFragment : Fragment() {
    private lateinit var model: Calculator
    private val adapter = HistoryAdapter(::onOperationClick, ::onOperationLongClick)
    private lateinit var binding: FragmentHistoryBinding
    private var TAG = HistoryFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.history)
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        model = Calculator(
            CalculatorDatabase.getInstance(requireContext()).operationDao()
        )
        binding = FragmentHistoryBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.rvHistoric.layoutManager = LinearLayoutManager(context)
        binding.rvHistoric.adapter = adapter

        getAllOperationsWs { updateHistory(it) }
        //model.getHistory { updateHistory(it) }
    }

    private fun onOperationClick(operation: OperationUi) {
        NavigationManager.goToOperationDetailFragment(parentFragmentManager, operation)
    }
    private fun onOperationLongClick(operation: OperationUi): Boolean {
        Toast.makeText(context, operation.getDate(), Toast.LENGTH_LONG).show()
        return false
    }
    private fun updateHistory(operations: List<OperationUi>) {
        CoroutineScope(Dispatchers.Main).launch {
            showHistory(operations.isNotEmpty())
            adapter.updateItems(operations)
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

    private fun getAllOperationsWs(callback: (List<OperationUi>) -> Unit) {
        data class GetAllOperationResponse(val uuid: String, val expression: String, val result: String, val timestamp: Long)

        CoroutineScope(Dispatchers.IO).launch {
            val request: Request = Request.Builder()
                .url("https://cm-calculadora.herokuapp.com/api/operations")
                .addHeader("apikey", "8270435acfead39ccb03e8aafbf37c49359dfbbcac4ef4769ae82c9531da0e17").build()

            val response = OkHttpClient().newCall(request).execute().body
            if (response != null) {
                val responseObj = Gson().fromJson(response.string(), Array<GetAllOperationResponse>::class.java).toList()
                callback(responseObj.map{
                    OperationUi(it.uuid, it.expression, it.result, it.timestamp)
                })
            }
        }
    }
}