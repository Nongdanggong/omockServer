package com.omockServer.omockServer.converter.dto.packet.s2c.game

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray

class PlacedStone(
    val currentTurnPlayerId: Int,
    val x: Int,
    val y: Int,
    val nextTurnPlayerId: Int, // BLACK == 1, WHITE == 2
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(currentTurnPlayerId).toList())
        byteList.addAll(intToByteArray(x).toList())
        byteList.addAll(intToByteArray(y).toList())
        byteList.addAll(intToByteArray(nextTurnPlayerId).toList())

        return byteList.toByteArray()
    }
}
