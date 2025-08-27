package com.omockServer.omockServer.service.gameRoom

import com.omockServer.omockServer.converter.dto.packet.s2c.gameroom.GameRoomList
import com.omockServer.omockServer.service.User

class GameRoomManager {
    val roomMap: MutableMap<Int, GameRoom> =
        mutableMapOf()
    var count = 0

    fun createRoom(
        user: User,
        roomName: String,
    ): GameRoom {
        count += 1

        val gameRoom = GameRoom(id = count, name = roomName, managerUser = user)

        roomMap[gameRoom.id] = gameRoom

        return gameRoom
    }

    fun removeRoom(roomId: Int) = roomMap.remove(roomId)

    fun enterRoom(
        roomId: Int,
        user: User,
    ): GameRoom {
        roomMap[roomId]?.enter(user) ?: throw Exception()

        return roomMap[roomId]!!
    }

    fun isExistRoom(roomId: Int): Boolean = roomId in roomMap.keys

    fun exitRoom(
        roomId: Int,
        user: User,
    ) {
        roomMap[roomId]?.exit(user) ?: throw Exception()

        if (roomMap[roomId]!!.userList.isEmpty()) removeRoom(roomId)
    }

    fun getRoomList(): GameRoomList =
        GameRoomList(
            roomList = roomMap.values.toList(),
        )
}
