package com.omockServer.omockServer.converter.dto.packet.c2s.game

import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPayload

class StartGame(
    val roomId: Int,
) : C2SPayload()
