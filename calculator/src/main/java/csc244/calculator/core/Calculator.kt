package csc244.calculator.core

import android.util.Log
import kotlin.math.log

class Calculator {
    private val statementList: MutableList<Statement> = mutableListOf();

    private var registerNum: Num? = null;

    private var operator: Operator? = null;

    fun register(num: Num): Statement? {
        if (registerNum == null) {
            registerNum = num;
            Log.d("registerNum", registerNum.toString())
            return null
        } else {
            // register num != null
            if (operator == null) return null

            // operator != null
            val expression = BinaryExpression(registerNum!!, num, operator!! as BinaryOperator)
            val statement = Statement(expression);
            statementList.add(statement)
            clear()

            return statement;
        }
    }

    fun setOperator(operator: Operator): Statement? {
        if (operator is UnaryOperator) {
            val expression = UnaryExpression(registerNum!!, operator);
            val statement = Statement(expression);
            statementList.add(statement)
            clear()

            return statement
        } else if (operator is BinaryOperator) {
            this.operator = operator
        }

        return null;
    }

    fun clear() {
        registerNum = null
        operator = null
    }
}