package csc244.calculator.core

import android.os.Parcel
import android.os.Parcelable

@Suppress("DEPRECATION")
class UnaryExpression(
    private val num: Num,
    private val operator: UnaryOperator
) : Expression(operator) {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Num::class.java.classLoader)!!,
        parcel.readParcelable(UnaryOperator::class.java.classLoader)!!
    )

    fun getNum(): Num {
        return num
    }

    override fun getOperator(): UnaryOperator {
        return operator
    }

    override fun toString(): String {
        return when (operator) {
            UnaryOperator.SquareRoot -> "√$num"
            UnaryOperator.Square -> "$num^2"
            UnaryOperator.Log -> "log($num)"
            UnaryOperator.Ln -> "ln($num)"
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(num, flags)
        parcel.writeParcelable(operator, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UnaryExpression> {
        override fun createFromParcel(parcel: Parcel): UnaryExpression {
            return UnaryExpression(parcel)
        }

        override fun newArray(size: Int): Array<UnaryExpression?> {
            return arrayOfNulls(size)
        }
    }
}

@Suppress("DEPRECATION")
class BinaryExpression(
    private val leftNum: Num,
    private val rightNum: Num,
    private val operator: BinaryOperator
) : Expression(operator) {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Num::class.java.classLoader)!!,
        parcel.readParcelable(Num::class.java.classLoader)!!,
        parcel.readParcelable(BinaryOperator::class.java.classLoader)!!
    )

    fun getRightNum(): Num {
        return rightNum
    }

    fun getLeftNum(): Num {
        return leftNum
    }

    override fun getOperator(): BinaryOperator {
        return operator
    }

    override fun toString(): String {
        return when (operator) {
            BinaryOperator.Plus -> "$leftNum + $rightNum"
            BinaryOperator.Minus -> "$leftNum - $rightNum"
            BinaryOperator.Multiply -> "$leftNum * $rightNum"
            BinaryOperator.Divide -> "$leftNum / $rightNum"
            BinaryOperator.NPower -> "$leftNum ^ $rightNum"
            BinaryOperator.NSquareRoot -> "$rightNum√$leftNum"
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(leftNum, flags)
        parcel.writeParcelable(rightNum, flags)
        parcel.writeParcelable(operator, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BinaryExpression> {
        override fun createFromParcel(parcel: Parcel): BinaryExpression {
            return BinaryExpression(parcel)
        }

        override fun newArray(size: Int): Array<BinaryExpression?> {
            return arrayOfNulls(size)
        }
    }
}

abstract class Expression(private val operator: Operator) : Parcelable {
    open fun getOperator(): Operator {
        return operator
    }
}