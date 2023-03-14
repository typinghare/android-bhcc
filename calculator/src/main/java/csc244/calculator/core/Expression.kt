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

    override fun toString(): String {
        return when (operator) {
            UnaryOperator.SquareRoot -> "√$num"
            UnaryOperator.Square -> "$num^2"
            UnaryOperator.Log -> "log($num)"
            UnaryOperator.Ln -> "ln($num)"
        }
    }
}

class BinaryExpression(
    private val leftNum: Num,
    private val rightNum: Num,
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
        return when (operator) {
            BinaryOperator.Plus -> "$leftNum + $rightNum"
            BinaryOperator.Minus -> "$leftNum - $rightNum"
            BinaryOperator.Multiply -> "$leftNum * $rightNum"
            BinaryOperator.Divide -> "$leftNum / $rightNum"
            BinaryOperator.NPower -> "$leftNum ^ $rightNum"
            BinaryOperator.NSquareRoot -> "$rightNum√$leftNum"
        }
    }
}

abstract class Expression(private val operator: Operator) {
    open fun getOperator(): Operator {
        return operator;
    }
}