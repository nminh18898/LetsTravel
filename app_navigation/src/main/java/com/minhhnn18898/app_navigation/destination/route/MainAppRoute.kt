package com.minhhnn18898.app_navigation.destination.route

interface MainAppRoute {
    companion object {
        const val tripIdArg = "trip_id"
        const val hotelIdArg = "hotel_id"
        const val flightIdArg = "flight_id"
        const val activityIdArg = "activity_id"

        const val HOME_SCREEN_ROUTE = "home_screen_route"
        const val EDIT_TRIP_SCREEN_ROUTE = "edit_trip_screen_route"
        const val SAVED_TRIPS_LISTING_SCREEN_ROUTE = "saved_trips_listing_screen_route"
        const val TRIP_DETAIL_SCREEN_ROUTE = "trip_detail_screen_route"
        const val EDIT_FLIGHT_INFO_SCREEN_ROUTE = "edit_flight_info_screen_route"
        const val EDIT_HOTEL_INFO_SCREEN_ROUTE = "edit_hotel_info_screen_route"
        const val EDIT_TRIP_ACTIVITY_INFO_SCREEN_ROUTE = "edit_trip_activity_info_screen_route"
    }
}