package com.omockServer.omockServer.converter.dto.packet.c2s

import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPacketType

class C2SPacket(
    val packetType: C2SPacketType,
    val payload: C2SPayload?,
)
