package com.omockServer.omockServer.converter.dto.packet.c2s

import com.omockServer.omockServer.converter.dto.packet.c2s.game.PlaceOnStone
import com.omockServer.omockServer.converter.dto.packet.c2s.game.StartGame
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.CreateRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.EnterRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.ExitRoom

// enum class ByteSize(
//    val value: Int,
// ) {
//    IntSize(value = 4),
// }

// / 각 패킷 마다 고유한 처리
class DeSerializer {
    val INT_SIZE = 4
    val ENCODING_UTF_8 = Charsets.UTF_8

    fun byteArrayToInt(byteArray: ByteArray): Int =
        (byteArray[0].toInt() and 0xFF shl 24) or
            (byteArray[1].toInt() and 0xFF shl 16) or
            (byteArray[2].toInt() and 0xFF shl 8) or
            (byteArray[3].toInt() and 0xFF)

    fun getPacketSize(inputByte: ByteArray): Int {
        val packetSize = byteArrayToInt(inputByte.sliceArray(0..3))
        return packetSize
    }

    fun getPacketType(inputByte: ByteArray): C2SPacketType {
        val packetType =
            C2SPacketType.usingValue(
                byteArrayToInt(inputByte.sliceArray(4..7)),
            )
        return packetType
    }

    fun deserialize(inputByte: ByteArray): C2SPacket {
        // 1. packetSize
        val packetSize = getPacketSize(inputByte)

        // 2. packetType
        val packetType = getPacketType(inputByte)

        var payload: C2SPayload? = null

        if (packetSize > 8) {
            val payloadByteArray = inputByte.sliceArray(8 until packetSize)
            var offset = 0

            payload =
                when (packetType) {
                    C2SPacketType.ROOM_CREATE -> {
                        val roomNameSize = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        offset += INT_SIZE

                        val roomName =
                            String(payloadByteArray, offset, roomNameSize, ENCODING_UTF_8)

                        CreateRoom(roomNameSize = roomNameSize, roomName = roomName)
                    }
                    C2SPacketType.ROOM_ENTER -> {
                        val roomId = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        EnterRoom(roomId = roomId)
                    }
                    C2SPacketType.ROOM_EXIT -> {
                        val roomId = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        ExitRoom(roomId = roomId)
                    }
                    C2SPacketType.START_GAME -> {
                        val roomId = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        StartGame(roomId = roomId)
                    }
                    C2SPacketType.PLACE_ON_STONE -> {
                        val gameId = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        offset += INT_SIZE

                        val playerId = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        offset += INT_SIZE

                        val x = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))
                        offset += INT_SIZE

                        val y = byteArrayToInt(payloadByteArray.sliceArray(offset until offset + INT_SIZE))

                        PlaceOnStone(
                            gameId = gameId,
                            playerId = playerId,
                            x = x,
                            y = y,
                        )
                    }
                    else -> {
                        null
                    }
                }
        }

//            when (packetType) {
//                S2CPacketType.S2C_GAME_ROOM_LIST -> return deserializeGameRoomList(
//                    packetSize - 8,
//                    inputByte.sliceArray(8..inputByte.size - 1),
//                )

//            }
        return C2SPacket(
            packetType = packetType,
            packetSize = packetSize,
            payload = payload,
        )
    }

    // S2C_GAME_ROOM_LIST
//        fun deserializeGameRoomList(
//            payloadByteSize: Int,
//            payloadByteArray: ByteArray,
//        ): GameRoomList {
//            val numberOfRooms = ByteArrayToInt(payloadByteArray.sliceArray(0..3))
//            val roomList = mutableListOf<Room>()
//
//            for (index in 0 until numberOfRooms) {
//                val offset = ByteSize.IntSize.value + index * (ByteSize.IntSize.value * 2) // numberOfRooms 의 크기 + 앞선 Room 데이터 크기
//
//                roomList.add(
//                    Room(
//                        id = ByteArrayToInt(payloadByteArray.sliceArray(offset until offset + ByteSize.IntSize.value)),
//                        currentPeopleNumber =
//                            ByteArrayToInt(
//                                payloadByteArray.sliceArray(offset + ByteSize.IntSize.value until offset + ByteSize.IntSize.value * 2),
//                            ),
//                    ),
//                )
//            }
//
//            return GameRoomList(
//                numberOfRooms = numberOfRooms,
//                roomList = roomList,
//            )
//        }
}
