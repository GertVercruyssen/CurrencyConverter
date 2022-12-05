package com.example.currencyconverter.model

/**
 * Model for the data
 * @timestamp is not the timestamp returned from the API, but created locally
 */
data class RatesData(
    var rates: List<Pair<String,Double>> = listOf(),
    var timestamp:Long = 0
)