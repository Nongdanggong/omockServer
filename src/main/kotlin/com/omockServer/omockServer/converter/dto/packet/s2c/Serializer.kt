package com.omockServer.omockServer.converter.dto.packet.s2c

// // 각 패킷 마다 고유한 처리
class Serializer {
    companion object {
        // // 기본 타입들에 대한 byte화 로직 (비트연산)
        fun intToByteArray(target: Int): ByteArray =
            byteArrayOf( // 빅 엔디안 방식
                (target shr 24 and 0xFF).toByte(),
                (target shr 16 and 0xFF).toByte(),
                (target shr 8 and 0xFF).toByte(),
                (target and 0xFF).toByte(),
            )
    }

    // 패킷 정보를 추가 - prefix byte 생성: 패킷 길이(4byte), 패킷 종류(4byte)
    fun packetize(
        packetType: S2CPacketType,
        byteArray: ByteArray,
    ): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(byteArray.size + 8).toList())
        byteList.addAll(intToByteArray(packetType.value).toList())
        byteList.addAll(byteArray.toList())

        return byteList.toByteArray()
    }

    fun serialize(
        packetType: S2CPacketType,
        target: S2CPayload?,
    ): ByteArray {
        val payloadByteArray = target?.serialize() ?: byteArrayOf()

        return packetize(
            packetType = packetType,
            byteArray = payloadByteArray,
        )
    }

//        // S2C_GAME_ROOM_LIST
//        fun serializeGameRoomList(target: GameRoomList): ByteArray {
//            val byteList = mutableListOf<Byte>()
//
//            byteList.addAll(intToByteArray(target.numberOfRooms).toList())
//
//            for (room: Room in target.roomList) {
//                byteList.addAll(intToByteArray(room.id).toList())
//                byteList.addAll(intToByteArray(room.currentPeopleNumber).toList())
//            }
//
//            return byteList.toByteArray()
//        }
}
