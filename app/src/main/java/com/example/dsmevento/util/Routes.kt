package com.example.dsmevento.util

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val HISTORY = "history"
    const val CREATE_EDIT_EVENT = "create_edit_event"
    const val EVENT_DETAIL = "event_detail"
    const val REVIEWS = "reviews"

    fun createEditEventRoute(eventId: String = "new") = "$CREATE_EDIT_EVENT/$eventId"
    fun eventDetailRoute(eventId: String) = "$EVENT_DETAIL/$eventId"
    fun reviewsRoute(eventId: String) = "$REVIEWS/$eventId"
}