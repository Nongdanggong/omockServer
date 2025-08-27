package com.omockServer.omockServer.service.game

import com.omockServer.omockServer.converter.dto.packet.s2c.game.GameStartSetting
import com.omockServer.omockServer.service.User

class GameManager {
    val gameMap: MutableMap<Int, Game> = mutableMapOf()

    fun startNewGame(
        gameRoomId: Int,
        firstUser: User,
        secondUser: User,
    ): GameStartSetting {
        gameMap[gameRoomId] =
            Game(
                gameId = gameRoomId,
                firstUser = firstUser,
                secondUser = secondUser,
            )

        val createdGame = gameMap[gameRoomId]!!

        return GameStartSetting(
            createdGame = createdGame,
        )
    }

    fun endGame(gameRoomId: Int) {
        gameMap.remove(gameRoomId)
    }

    fun placeStoneAndCheckWin(
        gameRoomId: Int,
        playerId: Int,
        x: Int,
        y: Int,
    ): Boolean { // 돌을 놓은 유저가 이겼을 시 true
        val targetGame = gameMap[gameRoomId]

        val isWin = targetGame?.placeStoneAndCheckWin(x = x, y = y, playerId = playerId) ?: throw Exception()

        return isWin
    }

    fun getNowTurnPlayerId(gameId: Int): Int {
        val targetGame = gameMap[gameId]
        return targetGame!!.getNowTurnPlayerId()
    }
}
