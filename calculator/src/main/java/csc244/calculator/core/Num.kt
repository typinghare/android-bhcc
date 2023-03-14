package csc244.calculator.core

import android.util.Log
import java.math.BigDecimal
import java.math.MathContext

class Num(
    private val number: Number,
    private val type: NumType,
    private var precision: Int = 0
) {
    companion object {
        fun long(number: Number): Num {
            return Num(number, NumType.LONG, 0)
        }

        fun double(number: Number, precision: Int = -1): Num {
            val num = Num(number, NumType.DOUBLE, precision)

            if (precision == -1) {
                val doubleStr = BigDecimal(number.toDouble()).round(MathContext(8)).toString()
                Log.d("doubleStr", doubleStr)
                val ss = doubleStr.split('.')
                if (ss.size == 2) {
                    var str = ss[1]
                    while (str.isNotEmpty() && str[str.length - 1] == '0') {
                        str = str.substring(0, str.length - 1)
                    }
                    num.precision = str.length
                } else {
                    num.precision = 0
                }
            }

            return num
        }
    }

    fun isLong(): Boolean {
        return type == NumType.LONG
    }

    fun isDouble(): Boolean {
        return type == NumType.DOUBLE
    }

    fun toLong(): Long {
        return number.toLong()
    }

    fun toDouble(): Double {
        return number.toDouble()
    }

    fun getPrecision(): Int {
        return precision
    }

    private fun toLongStr(): String {
        return toLong().toString()
    }

    fun toDoubleStr(): String {
        return if (precision == 0) {
            toLongStr() + "."
        } else {
            val integerPartLength = toLongStr().length
            val len = integerPartLength + precision
            val bigDecimal = BigDecimal(toDouble())
            bigDecimal.round(MathContext(len)).toString()
        }
    }

    override fun toString(): String {
        return if (isLong()) toLongStr() else toDoubleStr()
    }
}

enum class NumType { LONG, DOUBLE }