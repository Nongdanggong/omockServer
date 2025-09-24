package com.omockServer.omockServer.service

import com.omockServer.omockServer.service.game.Game
import com.omockServer.omockServer.service.gameRoom.GameRoom
import com.omockServer.omockServer.service.lobby.Lobby

data class User(
    val userId: Int,
    // TODO 유저 위치 관리
    val state: UserState = UserState.IN_LOBBY,
    val currentLobby: Lobby? = null,
    val currentGameRoom: GameRoom? = null,
    val currentGame: Game? = null,
)
