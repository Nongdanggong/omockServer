package com.omockServer.omockServer.converter.dto.packet.s2c.game

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray

class GameWinner(
    val winnerPlayerId: Int,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(winnerPlayerId).toList())

        return byteList.toByteArray()
    }
}
