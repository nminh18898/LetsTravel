@file:OptIn(ExperimentalMaterial3Api::class)

package com.minhhnn18898.letstravel.tripdetail.ui.edit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class EditFlightInfoViewModel: ViewModel() {
    var flightNumber by mutableStateOf("")
        private set

    var operatedAirlines by mutableStateOf("")
        private set

    var prices by mutableStateOf("")
        private set

    private var airportCodes: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    private var airportNames: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    private var airportCities: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    var flightDate by mutableStateOf<Long?>(null)
        private set

    private var flightTime: Map<ItineraryType, MutableState<Pair<Int, Int>>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(Pair(0, 0)),
        ItineraryType.ARRIVAL to mutableStateOf(Pair(0, 0))
    )

    fun onFlightNumberUpdated(value: String) {
        flightNumber = value
    }

    fun onAirlinesUpdated(value: String) {
        operatedAirlines = value
    }

    fun onPricesUpdated(value: String) {
        prices = value
    }

    fun getAirportCode(itineraryType: ItineraryType): String {
        return airportCodes[itineraryType]?.value ?: ""
    }

    fun onAirportCodeUpdated(itineraryType: ItineraryType, value: String) {
        airportCodes[itineraryType]?.value = value
    }

    fun getAirportName(itineraryType: ItineraryType): String {
        return airportNames[itineraryType]?.value ?: ""
    }

    fun onAirportNameUpdated(itineraryType: ItineraryType, value: String) {
        airportNames[itineraryType]?.value = value
    }

    fun getAirportCity(itineraryType: ItineraryType): String {
        return airportCities[itineraryType]?.value ?: ""
    }

    fun onAirportCityUpdated(itineraryType: ItineraryType, value: String) {
        airportCities[itineraryType]?.value = value
    }

    fun onFlightDateUpdated(value: Long?) {
        flightDate = value
    }

    fun getFlightTime(itineraryType: ItineraryType): Pair<Int, Int> {
        return flightTime[itineraryType]?.value ?: Pair(0, 0)
    }

    fun onFlightTimeUpdated(itineraryType: ItineraryType, value: Pair<Int, Int>) {
        flightTime[itineraryType]?.value = value
    }

    enum class ItineraryType {
        DEPARTURE,
        ARRIVAL
    }
}