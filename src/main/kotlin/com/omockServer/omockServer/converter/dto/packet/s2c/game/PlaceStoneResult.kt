package com.omockServer.omockServer.converter.dto.packet.s2c.game

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray

class PlaceStoneResult(
    val currentTurnPlayerId: Int,
    val x: Int,
    val y: Int,
    val nextTurnPlayerId: Int,
    val gameStatus: GameStatus, // ONGOING(0), WIN(1),
    val winnerPlayerId: Int? = null,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(currentTurnPlayerId).toList())
        byteList.addAll(intToByteArray(x).toList())
        byteList.addAll(intToByteArray(y).toList())
        byteList.addAll(intToByteArray(nextTurnPlayerId).toList())
        byteList.addAll(intToByteArray(gameStatus.value).toList())
        if (gameStatus == GameStatus.WIN) {
            byteList.addAll(intToByteArray(winnerPlayerId!!).toList())
        }

        return byteList.toByteArray()
    }
}
