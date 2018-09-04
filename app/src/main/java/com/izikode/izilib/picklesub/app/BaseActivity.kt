package com.izikode.izilib.picklesub.app

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.izikode.izilib.picklesubannotations.ConditionallySubscribe

abstract class BaseActivity : AppCompatActivity() {

    @ConditionallySubscribe(mustSatisfy = "isPositive")
    fun onBasePositiveReceived(positiveNumber: Num) {
        Log.d(LOG, "onBasePositiveReceived: ${positiveNumber.value}")
    }

    @ConditionallySubscribe(mustSatisfy = "isNegative")
    fun onNegativeReceived(negativeNumber: Num) {
        Log.d(LOG, "onNegativeReceived: ${negativeNumber.value}")
    }

    fun isPositive(number: Num) = number.value > 0

    fun isNegative(number: Num) = number.value < 0

    companion object {
        const val LOG = "PickleSubApp:BASE"
    }

}