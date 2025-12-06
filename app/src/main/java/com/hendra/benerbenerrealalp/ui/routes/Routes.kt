package com.hendra.benerbenerrealalp.ui.routes

sealed class Routes(val route: String) {

    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home") // Dashboard utama
    object Finance : Routes("finance")
    object Todo : Routes("todo")
    object Sleep : Routes("sleep")
}