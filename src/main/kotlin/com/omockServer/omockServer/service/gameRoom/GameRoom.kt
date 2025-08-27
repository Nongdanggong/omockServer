package com.omockServer.omockServer.service.gameRoom

import com.omockServer.omockServer.service.User

data class GameRoom(
    val id: Int,
    val name: String,
    val managerUser: User,
) {
    val userList: MutableList<User> = mutableListOf()
    val status: GameRoomStatusType = GameRoomStatusType.WAITING

    init {
        userList.add(managerUser)
    }

    fun isPassibleToEnter(): Boolean = userList.size < 2

    fun isPassibleGameStart(): Boolean = userList.size == 2

    fun enter(user: User) {
        if (isPassibleToEnter()) {
            userList.add(user)
        }
    }

    fun exit(user: User) {
        userList.remove(user)
    }
}
