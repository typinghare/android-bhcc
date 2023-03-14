package csc244.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import csc244.calculator.core.BinaryOperator
import csc244.calculator.core.Calculator
import csc244.calculator.core.Num
import csc244.calculator.core.Operator
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private val calculator = Calculator()

    private var num: Num = Num.long(0)

    private var isOperatorSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonList: MutableList<Button> = mutableListOf()
        buttonList.add(findViewById(R.id.btn_number_0))
        buttonList.add(findViewById(R.id.btn_number_1))
        buttonList.add(findViewById(R.id.btn_number_2))
        buttonList.add(findViewById(R.id.btn_number_3))
        buttonList.add(findViewById(R.id.btn_number_4))
        buttonList.add(findViewById(R.id.btn_number_5))
        buttonList.add(findViewById(R.id.btn_number_6))
        buttonList.add(findViewById(R.id.btn_number_7))
        buttonList.add(findViewById(R.id.btn_number_8))
        buttonList.add(findViewById(R.id.btn_number_9))

        for ((digit, button) in buttonList.withIndex()) {
            button.setOnClickListener { appendDigit(digit) }
        }

        val btnDot: Button = findViewById(R.id.btn_dot)
        btnDot.setOnClickListener { appendDot() }

        val btnPlus: Button = findViewById(R.id.btn_plus)
        val btnMinus: Button = findViewById(R.id.btn_minus)
        val btnMultiply: Button = findViewById(R.id.btn_multiply)
        val btnDivide: Button = findViewById(R.id.btn_divide)

        btnPlus.setOnClickListener { setOperator(BinaryOperator.PLUS) }
        btnMinus.setOnClickListener { setOperator(BinaryOperator.MINUS) }
        btnMultiply.setOnClickListener { setOperator(BinaryOperator.MULTIPLY) }
        btnDivide.setOnClickListener { setOperator(BinaryOperator.DIVIDE) }
    }

    private fun display(text: String) {
        val textDisplay = findViewById<TextView>(R.id.text_display)

        text.also { textDisplay.text = it }
    }

    private fun displayNum(num: Num) {
        val text: String = if (num.isLong()) num.toLong().toString() else num.toDouble().toString()
        display(text)
    }

    private fun appendDigit(digit: Int) {
        if (num.isLong() && num.toLong() == 0L) {
            // replace
            num = Num.long(digit)
        } else if (num.isLong()) {
            // appends
            num = Num.long(num.toLong() * 10 + digit)
        } else if (num.isDouble()) {
            val numStr: String = num.toDouble().toString() + digit.toString()
            num = Num.double(numStr.toDouble())
        }

        Log.d("num", num.toString())
        displayNum(num)
    }

    private fun appendDot() {

    }

    private fun setOperator(operator: Operator) {
        val statement = calculator.register(num)
        if (statement == null) {
            val statementAfter = calculator.setOperator(operator)
            if (statementAfter == null) {
                num = Num.long(0)
            } else {
                num = statementAfter.getResult()
                calculator.register(num)
                displayNum(num)
            }
        } else {
            num = statement.getResult()
            calculator.register(num)
            displayNum(num)
            val statementAfter = calculator.setOperator(operator)
            if (statementAfter == null) {
                num = Num.long(0)
            } else {
                num = statementAfter.getResult()
                calculator.register(num)
                displayNum(num)
            }
        }
    }
}