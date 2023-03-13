package csc244.calculator.core

import kotlin.math.pow

class Statement(
    private val expression: Expression,
) {
    private val result: Num

    init {
        // computes
        when (expression) {
            is UnaryExpression -> {
                result = when (expression.getOperator()) {
                    UnaryOperator.SQUARE -> computeSquare(expression.getNum())
                    UnaryOperator.LOG -> computeSquare(expression.getNum())
                    UnaryOperator.LN -> computeSquare(expression.getNum())
                }
            }
            is BinaryExpression -> {
                result = when (expression.getOperator()) {
                    BinaryOperator.PLUS -> computePlus(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.MINUS -> computeMinus(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.MULTIPLY -> computeMultiply(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.DIVIDE -> computeDivide(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.POWER -> computePower(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                }
            }
            else -> {
                throw IllegalArgumentException("Expression given is not recognized.")
            }
        }
    }

    fun getResult(): Num {
        return result
    }

    override fun toString(): String {
        return "$expression = $result"
    }

    private fun computeSquare(num: Num): Num {
        return if (num.isLong()) {
            Num.long(num.toDouble().pow(2))
        } else {
            Num.double(num.toDouble().pow(2))
        }
    }

    private fun computePlus(left: Num, right: Num): Num {
        return if (left.isLong() and right.isLong()) {
            Num.long(left.toLong() + right.toLong())
        } else {
            Num.double(left.toDouble() + right.toDouble())
        }
    }

    private fun computeMinus(left: Num, right: Num): Num {
        return if (left.isLong() and right.isLong()) {
            Num.long(left.toLong() - right.toLong())
        } else {
            Num.double(left.toDouble() - right.toDouble())
        }
    }

    private fun computeMultiply(left: Num, right: Num): Num {
        return if (left.isLong() and right.isLong()) {
            Num.long(left.toLong() * right.toLong())
        } else {
            Num.double(left.toDouble() * right.toDouble())
        }
    }

    private fun computeDivide(left: Num, right: Num): Num {
        return Num.long(left.toDouble() / right.toDouble())
    }

    private fun computePower(left: Num, right: Num): Num {
        return if (left.isLong() and right.isLong()) {
            Num.long(left.toDouble().pow(right.toDouble()))
        } else {
            Num.double(left.toDouble().pow(right.toDouble()))
        }
    }
}