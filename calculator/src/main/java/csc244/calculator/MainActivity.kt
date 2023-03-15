package csc244.calculator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import csc244.calculator.core.*
import csc244.calculator.model.MainViewModel

const val HISTORY_EXTRA = "csc244.calculator.MainActivity.HISTORY_EXTRA"

const val REQUEST_HISTORY = 1

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var mainViewModel: MainViewModel? = null

    private var calculator: MutableLiveData<Calculator> = MutableLiveData()

    private var num: MutableLiveData<Num> = MutableLiveData()

    private var numRegistered: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // restore the saved string value, if any
        val savedNum = savedInstanceState?.getParcelable("num") as Num?
        val savedCalculator = savedInstanceState?.getParcelable("calculator") as Calculator?

        // create view model provider
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        calculator = mainViewModel!!.getCalculatorData(savedCalculator)
        setNum(mainViewModel!!.getNumData(savedNum).value!!)
        numRegistered = mainViewModel!!.getNumRegisteredData(true)

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
            button.setTextColor(Color.parseColor("#333333"))
            button.setBackgroundColor(Color.parseColor("#cccccc"))
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
            if (numRegistered.value!!) {
                setOperator(BinaryOperator.Multiply)
                numRegistered.value = false
                num.value = Num.long(-1)
                findAndDisplay()
            }
        }

        // equal
        val btnEqual: Button = findViewById(R.id.btn_equal)
        btnEqual.setOnClickListener { findAndDisplay() }

        // clear
        val btnClear: Button = findViewById(R.id.btn_clear)
        btnClear.setOnClickListener {
            calculator.value!!.clear()
            setNum(Num.long(0))
            numRegistered.value = true
        }

        // history
        val btnHistory: Button = findViewById(R.id.btn_history)
        btnHistory.setOnClickListener {
            // show history activity
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.putStringArrayListExtra(HISTORY_EXTRA, calculator.value!!.getHistory())
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("num", num.value!!)
        outState.putParcelable("calculator", calculator.value!!)
    }

    @Deprecated("Deprecated in Java")
    @SuppressWarnings("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult", "onActivityResult")
        if (requestCode == REQUEST_HISTORY && resultCode == Activity.RESULT_OK && data != null) {
            val selectedId: Int = data.getIntExtra(HistoryActivity.SELECTED_ID, -1)
            Log.d("id@onActivityResult", selectedId.toString())
            if (selectedId >= 0) {
                val statement = calculator.value!!.getStatement(selectedId)
                calculator.value!!.clear()
                calculator.value!!.register(statement.getResult())
            }
        }
    }

    private fun display(text: String) {
        val textDisplay = findViewById<TextView>(R.id.number_display)

        text.also { textDisplay.text = it }
    }

    private fun setNum(num: Num, toDisplay: Boolean = true) {
        this.num.value = num

        if (toDisplay) {
            val text: String = num.toString()
            display(text)
        }
    }

    private fun appendDigit(digit: Int) {
        numRegistered.value = false

        if (num.value!!.isLong() && num.value!!.toLong() == 0L) {
            // 0 => replace
            num.value = Num.long(digit)
        } else if (num.value!!.isLong()) {
            // long => directly appends
            num.value = Num.long(num.value!!.toLong() * 10 + digit)
        } else if (num.value!!.isDouble()) {
            val numStr: String = num.value!!.toDoubleStr() + digit.toString()
            num.value = Num.double(numStr.toDouble(), num.value!!.getPrecision() + 1)
        }

        setNum(num.value!!)
    }

    private fun appendDot() {
        if (num.value!!.isLong()) {
            setNum(Num.double(num.value!!.toLong(), 0))
            Log.d("precision", num.value!!.getPrecision().toString())
        }
    }

    /**
     * Finds the result and display it.
     */
    private fun findAndDisplay() {
        if (numRegistered.value!!) return

        val statement = calculator.value!!.register(num.value!!)
        Log.d("statement", statement.toString())

        if (statement != null) {
            setNum(statement.getResult())
            calculator.value!!.register(num.value!!)
        }

        numRegistered.value = true
    }

    /**
     * Sets operator.
     */
    private fun setOperator(operator: Operator) {
        if (!numRegistered.value!!) findAndDisplay()

        val statement = calculator.value!!.setOperator(operator)
        Log.d("statement", statement.toString())

        if (statement == null) {
            setNum(Num.long(0), toDisplay = false)
        } else {
            setNum(statement.getResult())
            calculator.value!!.register(num.value!!)
        }
    }
}