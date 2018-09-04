package com.izikode.izilib.picklesub.app

import java.util.*

data class Num(val value: Int) {

    companion object {
        fun random(min: Int, max: Int) = Num(Random().nextInt(max - min + 1) + min)
    }

}