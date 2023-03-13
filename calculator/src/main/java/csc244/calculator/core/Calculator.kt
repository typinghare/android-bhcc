package csc244.calculator.core

class Calculator {
    private val statementList: MutableList<Statement> = mutableListOf();

    private var registerNum: Num? = null;

    private var operator: Operator? = null;

    fun register(number: Number, type: NumType): Statement? {
        val num = Num(number, type);
        if (registerNum == null) {
            registerNum = num;
        } else {
            // register num != null
            if (operator == null) {
                throw IllegalArgumentException("Operator is null!")
            }

            // operator != null
            val expression = BinaryExpression(registerNum!!, num, operator!! as BinaryOperator)
            val statement = Statement(expression);
            statementList.add(statement)
            clear()

            return statement;
        }

        return null
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