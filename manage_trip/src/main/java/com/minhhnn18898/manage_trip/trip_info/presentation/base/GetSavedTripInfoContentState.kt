package com.minhhnn18898.manage_trip.trip_info.presentation.base


interface GetSavedTripInfoContentState

class GetSavedTripInfoContentLoading: GetSavedTripInfoContentState

class GetSavedTripInfoContentResult(val listTripItem: List<TripInfoItemDisplay>): GetSavedTripInfoContentState

class GetSavedTripInfoContentError: GetSavedTripInfoContentState

fun GetSavedTripInfoContentState.isContentLoading(): Boolean = this is GetSavedTripInfoContentLoading
fun GetSavedTripInfoContentState.hasResult(): Boolean = this is GetSavedTripInfoContentResult
fun GetSavedTripInfoContentState.getResult(): List<TripInfoItemDisplay> = (this as? GetSavedTripInfoContentResult)?.listTripItem ?: emptyList()
fun GetSavedTripInfoContentState.hasError(): Boolean = this is GetSavedTripInfoContentError