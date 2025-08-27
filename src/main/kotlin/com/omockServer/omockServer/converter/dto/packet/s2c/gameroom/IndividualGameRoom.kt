package com.omockServer.omockServer.converter.dto.packet.s2c.gameroom

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray
import com.omockServer.omockServer.service.gameRoom.GameRoom

class IndividualGameRoom(
    val room: GameRoom,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(room.id).toList())

        byteList.addAll(intToByteArray(room.name.toByteArray().size).toList())
        byteList.addAll(room.name.toByteArray().toList())

        byteList.addAll(intToByteArray(room.userList.size).toList())

        return byteList.toByteArray()
    }
}
