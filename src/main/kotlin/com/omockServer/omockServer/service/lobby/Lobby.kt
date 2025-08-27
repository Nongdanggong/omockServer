package com.omockServer.omockServer.service.lobby

import com.omockServer.omockServer.service.User

class Lobby(
    val lobbyId: Int,
) {
    val userList: MutableList<User> = mutableListOf()

    fun enter(user: User) {
        userList.add(user)
    }

    fun exit(user: User) {
        userList.remove(user)
    }
}
