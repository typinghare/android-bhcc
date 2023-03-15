package csc244.calculator.core

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.math.BigDecimal
import java.math.MathContext

class Num(
    private val number: Number,
    private val type: NumType,
    private var precision: Int = 0
) : Parcelable {
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

        @JvmField
        val CREATOR: Parcelable.Creator<Num> = object : Parcelable.Creator<Num> {
            override fun createFromParcel(parcel: Parcel): Num {
                val number = if (parcel.readByte().toInt() == 0) {
                    parcel.readLong()
                } else {
                    parcel.readDouble()
                }
                val type = NumType.valueOf(parcel.readString()!!)
                val precision = parcel.readInt()
                return Num(number, type, precision)
            }

            override fun newArray(size: Int): Array<Num?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        if (parcel.readByte().toInt() == 0) {
            parcel.readLong()
        } else {
            parcel.readDouble()
        },
        NumType.valueOf(parcel.readString()!!),
        parcel.readInt()
    )

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

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        if (isLong()) {
            dest.writeByte((0))
            dest.writeLong(toLong())
        } else {
            dest.writeByte((1))
            dest.writeDouble(toDouble())
        }
        dest.writeString(type.name)
        dest.writeInt(precision)
    }
}

enum class NumType { LONG, DOUBLE }