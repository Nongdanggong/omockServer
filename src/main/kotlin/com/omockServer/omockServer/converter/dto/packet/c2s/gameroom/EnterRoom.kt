package com.omockServer.omockServer.converter.dto.packet.c2s.gameroom

import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPayload

class EnterRoom(
    val roomId: Int,
) : C2SPayload()
