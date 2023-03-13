package csc244.calculator.core

class UnaryExpression(
    private val num: Num,
    private val operator: UnaryOperator
) : Expression(operator) {
    fun getNum(): Num {
        return num
    }

    override fun getOperator(): UnaryOperator {
        return operator
    }
}

class BinaryExpression(
    private val rightNum: Num,
    private val leftNum: Num,
    private val operator: BinaryOperator
) : Expression(operator) {
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
        return ""
    }
}

abstract class Expression(private val operator: Operator) {
    open fun getOperator(): Operator {
        return operator;
    }
}