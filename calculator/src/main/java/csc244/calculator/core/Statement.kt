package csc244.calculator.core

import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class Statement(
    private val expression: Expression,
) {
    private val result: Num

    init {
        // computes
        when (expression) {
            is UnaryExpression -> {
                result = when (expression.getOperator()) {
                    UnaryOperator.SquareRoot -> computeSquareRoot(expression.getNum())
                    UnaryOperator.Square -> computeSquare(expression.getNum())
                    UnaryOperator.Log -> computeLog(expression.getNum())
                    UnaryOperator.Ln -> computeLn(expression.getNum())
                }
            }
            is BinaryExpression -> {
                result = when (expression.getOperator()) {
                    BinaryOperator.Plus -> computePlus(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.Minus -> computeMinus(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.Multiply -> computeMultiply(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.Divide -> computeDivide(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.NPower -> computeNPower(
                        expression.getLeftNum(),
                        expression.getRightNum()
                    )
                    BinaryOperator.NSquareRoot -> computeNSquareRoot(
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

    private fun computeSquareRoot(num: Num): Num {
        return Num.double(sqrt(num.toDouble()))
    }

    private fun computeSquare(num: Num): Num {
        return if (num.isLong()) {
            Num.long(num.toDouble().pow(2))
        } else {
            Num.double(num.toDouble().pow(2))
        }
    }

    private fun computeLog(num: Num): Num {
        return Num.double(log(num.toDouble(), 10.toDouble()))
    }

    private fun computeLn(num: Num): Num {
        return Num.double(log(num.toDouble(), Math.E))
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
        return Num.double(left.toDouble() / right.toDouble())
    }

    private fun computeNPower(left: Num, right: Num): Num {
        return if (left.isLong() and right.isLong() && right.toLong() >= 0) {
            Num.long(left.toDouble().pow(right.toDouble()))
        } else {
            Num.double(left.toDouble().pow(right.toDouble()))
        }
    }

    private fun computeNSquareRoot(left: Num, right: Num): Num {
        return Num.double(left.toDouble().pow(1 / right.toDouble()))
    }
}