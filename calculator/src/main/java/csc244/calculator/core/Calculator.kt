package csc244.calculator.core

import android.os.Parcel
import android.os.Parcelable

@Suppress("DEPRECATION")
class Calculator : Parcelable {
    private val statementList: MutableList<Statement> = mutableListOf()

    private var registerNum: Num? = null

    private var operator: Operator? = null

    fun register(num: Num): Statement? {
        if (registerNum == null) {
            registerNum = num
            return null
        } else {
            // register num != null
            if (operator == null) return null

            // operator != null
            val expression = BinaryExpression(registerNum!!, num, operator!! as BinaryOperator)
            val statement = Statement(expression)
            statementList.add(statement)
            clear()

            return statement
        }
    }

    fun setOperator(operator: Operator): Statement? {
        if (operator is UnaryOperator) {
            val expression = UnaryExpression(registerNum!!, operator)
            val statement = Statement(expression)
            statementList.add(statement)
            
            clear()

            return statement
        } else if (operator is BinaryOperator) {
            this.operator = operator
        }

        return null
    }

    fun clear() {
        registerNum = null
        operator = null
    }

    fun getHistory(): ArrayList<String> {
        val stringList: ArrayList<String> = ArrayList()
        statementList.mapTo(stringList) { it.toString() }

        return stringList
    }

    fun getStatement(index: Int): Statement {
        return statementList[index]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(statementList)
        dest.writeParcelable(registerNum, flags)
        dest.writeParcelable(operator, flags)
    }

    companion object CREATOR : Parcelable.Creator<Calculator> {
        override fun createFromParcel(parcel: Parcel): Calculator {
            val statementList = mutableListOf<Statement>()
            parcel.readTypedList(statementList, Statement.CREATOR)

            val registerNum = parcel.readParcelable<Num>(Num::class.java.classLoader)
            val operator = parcel.readParcelable<Operator>(Operator::class.java.classLoader)
            val calculator = Calculator()

            calculator.statementList.addAll(statementList)
            calculator.registerNum = registerNum
            calculator.operator = operator

            return calculator
        }

        override fun newArray(size: Int): Array<Calculator?> {
            return arrayOfNulls(size)
        }
    }
}