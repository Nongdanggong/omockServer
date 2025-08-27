package com.omockServer.omockServer.service.lobby

import com.omockServer.omockServer.service.User

class LobbyManager {
    val lobbyMap: MutableMap<Int, Lobby> =
        mutableMapOf(Pair(0, Lobby(lobbyId = 0)))

    fun enter(
        roomNumber: Int? = 0,
        user: User,
    ) {
        lobbyMap[roomNumber]!!.enter(user)
    }

    fun exit(
        roomNumber: Int? = 0,
        user: User,
    ) {
        lobbyMap[roomNumber]!!.exit(user)
    }
}
