package csc244.calculator.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import csc244.calculator.core.Calculator
import csc244.calculator.core.Num

class MainViewModel : ViewModel() {
    private var calculatorData: MutableLiveData<Calculator> = MutableLiveData()

    private var numData: MutableLiveData<Num> = MutableLiveData()

    private var numRegisteredData: MutableLiveData<Boolean> = MutableLiveData()

    fun getCalculatorData(defaultCalculator: Calculator?): MutableLiveData<Calculator> {
        if (calculatorData.value == null) {
            calculatorData.value = defaultCalculator ?: Calculator()
        }

        return calculatorData
    }

    fun getNumData(defaultNum: Num?): MutableLiveData<Num> {
        if (numData.value == null) {
            numData.value = defaultNum ?: Num.long(0)
        }

        return numData
    }

    fun getNumRegisteredData(defaultNumRegistered: Boolean?): MutableLiveData<Boolean> {
        if (numRegisteredData.value == null) {
            numRegisteredData.value = defaultNumRegistered ?: true
        }

        return numRegisteredData
    }
}