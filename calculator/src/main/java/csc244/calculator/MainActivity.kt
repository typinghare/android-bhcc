package csc244.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import csc244.calculator.core.BinaryOperator
import csc244.calculator.core.Calculator
import csc244.calculator.core.Num
import csc244.calculator.core.Operator

class MainActivity : AppCompatActivity() {
    private val calculator = Calculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textDisplay = findViewById<TextView>(R.id.text_display)
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

        for ((number, button) in buttonList.withIndex()) {
            button.setOnClickListener {
                val text = textDisplay.text.toString()
                "$text$number".also { textDisplay.text = it }
            }
        }

        val btnPlus = findViewById<TextView>(R.id.btn_plus)
        val btnMinus = findViewById<TextView>(R.id.btn_minus)
        val btnMultiply = findViewById<TextView>(R.id.btn_multiply)
        val btnDivide = findViewById<TextView>(R.id.btn_divide)

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

    private fun setOperator(operator: Operator) {
        val statement = calculator.setOperator(operator) ?: return

        displayNum(statement.getResult())
    }
}