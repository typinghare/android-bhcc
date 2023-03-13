package csc244.calculator.core

sealed class BinaryOperator : Operator() {
    object PLUS : BinaryOperator()
    object MINUS : BinaryOperator()
    object MULTIPLY : BinaryOperator()
    object DIVIDE : BinaryOperator()
    object POWER : BinaryOperator()
}

sealed class UnaryOperator : Operator() {
    object SQUARE : UnaryOperator()
    object LOG : UnaryOperator()
    object LN : UnaryOperator()
}

sealed class Operator {
}