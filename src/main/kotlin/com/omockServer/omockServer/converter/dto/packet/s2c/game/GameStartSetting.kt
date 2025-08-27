package com.omockServer.omockServer.converter.dto.packet.s2c.game

import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray
import com.omockServer.omockServer.service.game.Game

class GameStartSetting(
    val createdGame: Game,
) : S2CPayload() {
    val gameId: Int = createdGame.gameId
    val firstPlayerId: Int = createdGame.firstPlayer.user.userId
    val secondPlayerId: Int = createdGame.secondPlayer.user.userId
    val firstPlayerStone: Int = createdGame.firstPlayer.stoneColor.value
    val secondPlayerStone: Int = createdGame.secondPlayer.stoneColor.value
    val startTurnPlayerId: Int = createdGame.getNowTurnPlayerId()

    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(gameId).toList())
        byteList.addAll(intToByteArray(firstPlayerId).toList())
        byteList.addAll(intToByteArray(secondPlayerId).toList())
        byteList.addAll(intToByteArray(firstPlayerStone).toList())
        byteList.addAll(intToByteArray(secondPlayerStone).toList())
        byteList.addAll(intToByteArray(startTurnPlayerId).toList())

        return byteList.toByteArray()
    }
}
