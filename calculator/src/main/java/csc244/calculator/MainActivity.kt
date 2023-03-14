package csc244.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import csc244.calculator.core.*

class MainActivity : AppCompatActivity() {
    private val calculator = Calculator()

    private var num: Num = Num.long(0)

    private var numRegistered: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // numbers
        val btnNumberList: MutableList<Button> = mutableListOf()
        btnNumberList.add(findViewById(R.id.btn_number_0))
        btnNumberList.add(findViewById(R.id.btn_number_1))
        btnNumberList.add(findViewById(R.id.btn_number_2))
        btnNumberList.add(findViewById(R.id.btn_number_3))
        btnNumberList.add(findViewById(R.id.btn_number_4))
        btnNumberList.add(findViewById(R.id.btn_number_5))
        btnNumberList.add(findViewById(R.id.btn_number_6))
        btnNumberList.add(findViewById(R.id.btn_number_7))
        btnNumberList.add(findViewById(R.id.btn_number_8))
        btnNumberList.add(findViewById(R.id.btn_number_9))

        for ((digit, button) in btnNumberList.withIndex()) {
            button.setOnClickListener { appendDigit(digit) }
        }

        // dot
        val btnDot: Button = findViewById(R.id.btn_dot)
        btnDot.setOnClickListener { appendDot() }

        // unary operators
        val btnSquareRoot: Button? = findViewById(R.id.btn_square_root)
        val btnSquare: Button? = findViewById(R.id.btn_square)
        val btnLog: Button? = findViewById(R.id.btn_log)
        val btnLn: Button? = findViewById(R.id.btn_ln)

        btnSquareRoot?.setOnClickListener { setOperator(UnaryOperator.SquareRoot) }
        btnSquare?.setOnClickListener { setOperator(UnaryOperator.Square) }
        btnLog?.setOnClickListener { setOperator(UnaryOperator.Log) }
        btnLn?.setOnClickListener { setOperator(UnaryOperator.Ln) }

        // binary operators
        val btnPlus: Button = findViewById(R.id.btn_plus)
        val btnMinus: Button = findViewById(R.id.btn_minus)
        val btnMultiply: Button = findViewById(R.id.btn_multiply)
        val btnDivide: Button = findViewById(R.id.btn_divide)
        val btnNPower: Button? = findViewById(R.id.btn_n_power)
        val btnNSquareRoot: Button? = findViewById(R.id.btn_n_square_root)

        btnPlus.setOnClickListener { setOperator(BinaryOperator.Plus) }
        btnMinus.setOnClickListener { setOperator(BinaryOperator.Minus) }
        btnMultiply.setOnClickListener { setOperator(BinaryOperator.Multiply) }
        btnDivide.setOnClickListener { setOperator(BinaryOperator.Divide) }
        btnNPower?.setOnClickListener { setOperator(BinaryOperator.NPower) }
        btnNSquareRoot?.setOnClickListener { setOperator(BinaryOperator.NSquareRoot) }

        // shift
        val btnMinusOrPlus: Button = findViewById(R.id.btn_minus_or_plus)
        btnMinusOrPlus.setOnClickListener {
            if (numRegistered) {
                setOperator(BinaryOperator.Multiply)
                numRegistered = false
                num = Num.long(-1)
                findAndDisplay()
            }
        }

        // equal
        val btnEqual: Button = findViewById(R.id.btn_equal)
        btnEqual.setOnClickListener { findAndDisplay() }

        // clear
        val btnClear: Button = findViewById(R.id.btn_clear)
        btnClear.setOnClickListener {
            calculator.clear()
            setNum(Num.long(0))
            numRegistered = true
        }

        // history
        val btnHistory: Button = findViewById(R.id.btn_history)
        btnHistory.setOnClickListener { }
    }

    private fun display(text: String) {
        val textDisplay = findViewById<TextView>(R.id.number_display)

        text.also { textDisplay.text = it }
    }

    private fun setNum(num: Num, toDisplay: Boolean = true) {
        this.num = num

        if (toDisplay) {
            val text: String = num.toString()
            display(text)
        }
    }

    private fun appendDigit(digit: Int) {
        numRegistered = false

        if (num.isLong() && num.toLong() == 0L) {
            // 0 => replace
            num = Num.long(digit)
        } else if (num.isLong()) {
            // long => directly appends
            num = Num.long(num.toLong() * 10 + digit)
        } else if (num.isDouble()) {
            val numStr: String = num.toDoubleStr() + digit.toString()
            num = Num.double(numStr.toDouble(), num.getPrecision() + 1)
        }

        setNum(num)
    }

    private fun appendDot() {
        if (num.isLong()) {
            setNum(Num.double(num.toLong(), 0))
            Log.d("precision", num.getPrecision().toString())
        }
    }

    /**
     * Finds the result and display it.
     */
    private fun findAndDisplay() {
        if (numRegistered) return

        val statement = calculator.register(num)
        Log.d("statement", statement.toString())

        if (statement != null) {
            setNum(statement.getResult())
            calculator.register(num)
        }

        numRegistered = true
    }

    /**
     * Sets operator.
     */
    private fun setOperator(operator: Operator) {
        if (!numRegistered) findAndDisplay()

        val statement = calculator.setOperator(operator)
        Log.d("statement", statement.toString())

        if (statement == null) {
            setNum(Num.long(0), toDisplay = false)
        } else {
            setNum(statement.getResult())
            calculator.register(num)
        }
    }
}