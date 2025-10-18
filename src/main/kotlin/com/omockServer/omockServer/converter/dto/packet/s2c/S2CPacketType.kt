package com.omockServer.omockServer.converter.dto.packet.s2c

enum class S2CPacketType(
    val value: Int,
) {
    // User
    GET_ME(value = 1),

    // Game Room
    ROOM_LIST(value = 5),
    ROOM_CREATED(value = 2),
    ROOM_REMOVED(value = 3),
//    ROOM_MODIFIED(value = 4),

    ROOM_NUMBER_OF_USER_MODIFIED(value = 4),

    // Individual Game Room
    GAME_ROOM_INFORMATION(value = 7),

    // Game
    GAME_START_SETTING(value = 100),
    PLACED_STONE(value = 101),
    PLACE_STONE_RESULT(value = 105),

    GAME_WINNER(value = 102),
    GAME_END(value = 103),

    // ACK Packet
    ROOM_EXIT_OK(value = 104),

    ENTER_LOBBY_OK(value = 106),

    CONNECTION_CLOSED_OK(value = 107),

    // ERROR Packet
    ERROR_ROOM_IS_FULL(value = 9000),
    ERROR_ROOM_NOT_EXIST(value = 9001),

    ;

    companion object {
        fun usingValue(value: Int): S2CPacketType {
            for (packetType in S2CPacketType.entries) {
                if (packetType.value == value) {
                    return packetType // 코드가 일치하는 enum 상수 반환
                }
            }
            error("value가 잘못되었습니다.")
        }
    }
}
