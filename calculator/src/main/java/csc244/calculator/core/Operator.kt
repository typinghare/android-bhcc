package csc244.calculator.core

sealed class BinaryOperator : Operator() {
    object Plus : BinaryOperator()
    object Minus : BinaryOperator()
    object Multiply : BinaryOperator()
    object Divide : BinaryOperator()
    object NSquareRoot: BinaryOperator()
    object NPower: BinaryOperator()
}

sealed class UnaryOperator : Operator() {
    object SquareRoot: UnaryOperator()
    object Square : UnaryOperator()
    object Log : UnaryOperator()
    object Ln : UnaryOperator()
}

sealed class Operator {
}