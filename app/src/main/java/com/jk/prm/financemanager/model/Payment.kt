package com.jk.prm.financemanager.model

import java.util.*

class Payment(
        var date: String,
        var name: String,
        var category: String,
        var amount: Amount
) {
    var id: String = UUID.randomUUID().toString()
}

class Amount(var value: Double) {

    override fun toString(): String {
        return "$value PLN"
    }
}