package com.omockServer.omockServer.service

import java.net.Socket

data class ClientSession(
    val id: Int,
    val socket: Socket,
)
