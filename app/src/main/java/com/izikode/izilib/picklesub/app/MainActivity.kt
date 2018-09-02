package com.izikode.izilib.picklesub.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.izikode.izilib.picklesub.PickleSub
import com.izikode.izilib.picklesubannotations.ConditionallySubscribe
import com.izikode.izilib.picklesubannotations.SimplySubscribe
import com.squareup.otto.Subscribe
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val bus = Bus(ThreadEnforcer.MAIN)
    val subscriber = MainActivitySubscriber(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bus.register(this)
        PickleSub.register(subscriber, bus)

        button.setOnClickListener {
            bus.post(random())
        }
    }

    override fun onDestroy() {
        bus.unregister(this)
        PickleSub.unregister(subscriber, bus)

        super.onDestroy()
    }

    @SimplySubscribe
    fun onNumberReceived(number: Num) {
        Log.d(LOG, "onNumberReceived: ${number.value}")
    }

    @Subscribe
    fun onNumberReceivedOtto(number: Num) {
        Log.d(LOG, "onNumberReceivedOtto: ${number.value}")
    }

    @ConditionallySubscribe(mustSatisfy = "isPositive")
    fun onPositiveReceived(positiveNumber: Num) {
        Log.d(LOG, "onPositiveReceived: ${positiveNumber.value}")
    }

    @Subscribe
    fun onPositiveReceivedOtto(positiveNumber: Num) {
        Log.d(LOG, "onPositiveReceivedOtto: ${positiveNumber.value}")
    }

    fun isPositive(number: Num) = number.value > 0

    data class Num(val value: Int)
    fun random() = Num(Random().nextInt(5 - (-5) + 1) + (-5))

    companion object {
        const val LOG = "PickleSubApp"
    }

}
