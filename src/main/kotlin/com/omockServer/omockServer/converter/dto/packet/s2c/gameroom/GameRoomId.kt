package com.omockServer.omockServer.converter.dto.packet.s2c.gameroom

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray

class GameRoomId(
    val roomId: Int,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(roomId).toList())

        return byteList.toByteArray()
    }
}
