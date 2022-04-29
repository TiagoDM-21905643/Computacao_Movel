package com.example.acalculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acalculator.databinding.FragmentCalculatorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculatorFragment : Fragment() {
    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var viewModel: CalculatorViewModel
    private val adapter = HistoryAdapter(::onOperationClick, ::onOperationLongClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)
        binding = FragmentCalculatorBinding.bind(view)
        viewModel = ViewModelProvider(this).get( CalculatorViewModel::class.java )
        binding.textVisor.text = viewModel.getDisplayValue()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        binding.button0.setOnClickListener { onClickNumberButton(0) }  // 0
        binding.button1.setOnClickListener { onClickNumberButton(1) }  // 1
        binding.button2.setOnClickListener { onClickNumberButton(2) }  // 2
        binding.button3.setOnClickListener { onClickNumberButton(3) }  // 3
        binding.button4.setOnClickListener { onClickNumberButton(4) }  // 4
        binding.button5.setOnClickListener { onClickNumberButton(5) }  // 5
        binding.button6.setOnClickListener { onClickNumberButton(6) }  // 6
        binding.button7.setOnClickListener { onClickNumberButton(7) }  // 7
        binding.button8.setOnClickListener { onClickNumberButton(8) }  // 8
        binding.button9.setOnClickListener { onClickNumberButton(9) }  // 9

        binding.buttonAdition.setOnClickListener        { onClickSymbol("+") }  // +
        binding.buttonSubtraction.setOnClickListener    { onClickSymbol("-") }  // -
        binding.buttonMultiplication.setOnClickListener { onClickSymbol("*") }  // *
        binding.buttonDivision.setOnClickListener       { onClickSymbol("/") }  // /
        binding.buttonDot.setOnClickListener            { onClickSymbol(".", checkSymbol = false) }  // .

        binding.buttonPercentage.setOnClickListener { onClickEquals(percentage = true) }  // %
        binding.buttonEquals.setOnClickListener     { onClickEquals() }  // =
        binding.buttonClear.setOnClickListener      { onClickClear() } // C
        binding.buttonDelete.setOnClickListener     { onClickDelete() }  // <

        binding.rvHistoric?.layoutManager = LinearLayoutManager(context)
        binding.rvHistoric?.adapter = adapter

        viewModel.onGetHistory { updateHistory(it) }
    }

    private fun onOperationClick(operation: OperationUi) {
        NavigationManager.goToOperationDetailFragment(parentFragmentManager, operation)
    }
    private fun onOperationLongClick(operation: OperationUi): Boolean {
        Toast.makeText(context, operation.getDate(), Toast.LENGTH_LONG).show()
        return false
    }

    private fun onClickNumberButton(num: Int) {
        binding.textVisor.text = viewModel.onClickNumber(num)
    }
    /*private fun onClickNumberButton(num: Int) {
        if (binding.textVisor.text.equals("0") || lastActionEquals || lastActionClear) {
            binding.textVisor.text = "$num"
        } else {
            binding.textVisor.append("$num")
        }
        setBoolValidators()
    }*/
    private fun onClickSymbol(symbol: String, checkSymbol: Boolean = true) {
        binding.textVisor.text = viewModel.onClickSymbol(symbol, checkSymbol)
    }
    /*@SuppressLint("SetTextI18n")
    private fun onClickSymbol(symbol: String, checkSymbol: Boolean = true) {
        if (checkSymbol && symbols.contains(binding.textVisor.text[binding.textVisor.text.length - 1])) {
            binding.textVisor.text = binding.textVisor.text.substring(0, binding.textVisor.text.length - 1)
        }
        when {
            errorActive -> binding.textVisor.text = "0$symbol"
            lastActionEquals -> binding.textVisor.text = symbol
            else -> binding.textVisor.append(symbol)
        }
        setBoolValidators()
    }*/
    private fun onClickEquals(percentage: Boolean = false) {
        val displayUpdated = viewModel.onClickEquals(percentage) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.onGetHistory { updateHistory(it) }
            }
        }
        binding.textVisor.text = displayUpdated
    }
    /*@SuppressLint("SetTextI18n")
    private fun onClickEquals(percentage: Boolean = false) {
        var result = ""
        if (errorActive) {
            result = "0"
        } else {
            val expressionStr = binding.textVisor.text.toString()
            val expression = ExpressionBuilder(expressionStr).build()
            try {
                if (percentage) {
                    if ((expression.evaluate() / 100) % 1 == 0.0) {
                        result = (expression.evaluate() / 100).roundToInt().toString()
                    } else {
                        result = (expression.evaluate() / 100).toString()
                    }
                } else {
                    if (expression.evaluate() % 1 == 0.0) {
                        result = expression.evaluate().roundToInt().toString()
                    } else {
                        result = expression.evaluate().toString()
                    }
                    operations.add(Operation(expressionStr, result))
                }
            } catch (e: Exception) {
                result = "ERROR"
                errorActive = true
            }
        }
        lastActionEquals = true
        lastActionClear = false

        binding.textVisor.text = result
        adapter.updateItems(operations.map { OperationUi(it.expression, it.result, it.timestamp) })
    }*/
    private fun onClickClear() {
        binding.textVisor.text = viewModel.onClickClear()
    }
    private fun onClickDelete() {
        binding.textVisor.text = viewModel.onClickDelete()
    }

    private fun updateHistory(operations: List<Operation>) {
        adapter.updateItems(operations.map { OperationUi(it.expression, it.result, it.timestamp) })
    }
}