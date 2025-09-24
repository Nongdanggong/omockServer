package com.omockServer.omockServer.converter.dto.packet.c2s

import com.omockServer.omockServer.converter.dto.packet.c2s.game.PlaceOnStone
import com.omockServer.omockServer.converter.dto.packet.c2s.game.StartGame
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.CreateRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.EnterRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.ExitRoom

// / 각 패킷 마다 고유한 처리
class DeSerializer {
    val INT_SIZE = 4
    val PACKET_SIZE_BYTE_SIZE = 4
    val PACKET_TYPE_BYTE_SIZE = 4
    val ENCODING_UTF_8 = Charsets.UTF_8

    fun byteArrayToInt(byteArray: ByteArray): Int =
        (byteArray[0].toInt() and 0xFF shl 24) or
            (byteArray[1].toInt() and 0xFF shl 16) or
            (byteArray[2].toInt() and 0xFF shl 8) or
            (byteArray[3].toInt() and 0xFF)

    fun getPacketSize(inputByte: ByteArray): Int {
        val packetSize = byteArrayToInt(inputByte.sliceArray(0 until PACKET_SIZE_BYTE_SIZE))
        return packetSize
    }

    fun getPacketType(inputByte: ByteArray): C2SPacketType {
        val packetType =
            C2SPacketType.usingValue(
                byteArrayToInt(inputByte.sliceArray(0 until PACKET_TYPE_BYTE_SIZE)),
            )
        return packetType
    }

    fun getFrameList(resizedBuffer: ByteArray): List<ByteArray> {
        val totalBufferSize = resizedBuffer.size

        var processedSize: Int = 0
        var remainBuffer: ByteArray = resizedBuffer.copyOf(totalBufferSize)

        val frameList = mutableListOf<ByteArray>()

        while (totalBufferSize > processedSize) {
            val packetSize = getPacketSize(remainBuffer)
            val totalPacketSize = PACKET_SIZE_BYTE_SIZE + packetSize

            frameList.add(
                remainBuffer.sliceArray(PACKET_SIZE_BYTE_SIZE until totalPacketSize),
            )

            remainBuffer = remainBuffer.sliceArray(totalPacketSize until remainBuffer.size)
            processedSize += totalPacketSize
        }

        return frameList
    }

    fun deserialize(inputByte: ByteArray): C2SPacket {
        val packetType = getPacketType(inputByte)

        var payload: C2SPayload? = null

        val payloadByteArray = inputByte.sliceArray(PACKET_TYPE_BYTE_SIZE until inputByte.size)
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

        return C2SPacket(
            packetType = packetType,
            payload = payload,
        )
    }
}
