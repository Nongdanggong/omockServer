package com.omockServer.omockServer.converter.dto.packet.c2s.gameroom

import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPayload

class CreateRoom(
    val roomNameSize: Int,
    val roomName: String,
) : C2SPayload()
