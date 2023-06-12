package com.example.qrcodemsn

sealed class Screen(
    val route: String
){
    object MainScreen: Screen("main_screen")
    object QrcodeScreen: Screen("qrcode_screen")
}
