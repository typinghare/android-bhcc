package csc244.calculator.core

class Num(
    private val number: Number,
    private val type: NumType,
) {
    companion object {
        fun long(number: Number): Num {
            return Num(number, NumType.LONG)
        }

        fun double(number: Number): Num {
            return Num(number, NumType.DOUBLE)
        }
    }

    fun isLong(): Boolean {
        return type == NumType.LONG;
    }

    fun isDouble(): Boolean {
        return type == NumType.DOUBLE;
    }

    fun toLong(): Long {
        return number.toLong();
    }

    fun toDouble(): Double {
        return number.toDouble();
    }
}

enum class NumType {
    LONG, DOUBLE;
}