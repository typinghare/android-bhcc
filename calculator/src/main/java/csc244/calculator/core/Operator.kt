package csc244.calculator.core

import android.os.Parcel
import android.os.Parcelable

sealed class BinaryOperator() : Operator() {
    object Plus : BinaryOperator()
    object Minus : BinaryOperator()
    object Multiply : BinaryOperator()
    object Divide : BinaryOperator()
    object NSquareRoot : BinaryOperator()
    object NPower : BinaryOperator()
}

sealed class UnaryOperator : Operator() {
    object SquareRoot : UnaryOperator()
    object Square : UnaryOperator()
    object Log : UnaryOperator()
    object Ln : UnaryOperator()
}

sealed class Operator : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Operator> {
            override fun createFromParcel(parcel: Parcel): Operator {
                return when (parcel.readInt()) {
                    0 -> BinaryOperator.Plus
                    1 -> BinaryOperator.Minus
                    2 -> BinaryOperator.Multiply
                    3 -> BinaryOperator.Divide
                    4 -> BinaryOperator.NSquareRoot
                    5 -> BinaryOperator.NPower
                    6 -> UnaryOperator.SquareRoot
                    7 -> UnaryOperator.Square
                    8 -> UnaryOperator.Log
                    9 -> UnaryOperator.Ln
                    else -> throw IllegalArgumentException("Unknown operator type")
                }
            }

            override fun newArray(size: Int): Array<Operator?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(getOperatorType())
    }

    private fun getOperatorType(): Int {
        return when (this) {
            is BinaryOperator.Plus -> 0
            is BinaryOperator.Minus -> 1
            is BinaryOperator.Multiply -> 2
            is BinaryOperator.Divide -> 3
            is BinaryOperator.NSquareRoot -> 4
            is BinaryOperator.NPower -> 5
            is UnaryOperator.SquareRoot -> 6
            is UnaryOperator.Square -> 7
            is UnaryOperator.Log -> 8
            is UnaryOperator.Ln -> 9
        }
    }
}