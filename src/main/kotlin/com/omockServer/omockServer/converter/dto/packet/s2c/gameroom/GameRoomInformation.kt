package com.omockServer.omockServer.converter.dto.packet.s2c.gameroom

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray
import com.omockServer.omockServer.service.gameRoom.GameRoom

class GameRoomInformation(
    val gameRoom: GameRoom,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(gameRoom.id).toList())

        byteList.addAll(intToByteArray(gameRoom.name.toByteArray().size).toList())
        byteList.addAll(gameRoom.name.toByteArray().toList())

        byteList.addAll(intToByteArray(gameRoom.userList.size).toList())

        for (user in gameRoom.userList) {
            byteList.addAll(intToByteArray(user.userId).toList())
        }

        return byteList.toByteArray()
    }
}
