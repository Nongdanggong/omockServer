package com.omockServer.omockServer.converter.dto.packet.c2s.game

import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPayload

class PlaceOnStone(
    val gameId: Int,
    val playerId: Int,
    val x: Int,
    val y: Int,
) : C2SPayload()
