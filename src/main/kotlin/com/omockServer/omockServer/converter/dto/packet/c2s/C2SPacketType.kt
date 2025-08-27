package com.omockServer.omockServer.converter.dto.packet.c2s

enum class C2SPacketType(
    val value: Int,
) {
    GET_ROOM_LIST(value = 0),
    ROOM_CREATE(value = 1),
    ROOM_ENTER(value = 2), // 게임 대기실 입장 요청
    ROOM_EXIT(value = 3),

    START_GAME(value = 4),
    PLACE_ON_STONE(value = 5), // 돌을 놓음

    ENTER_LOBBY(value = 6),
    ;

    companion object {
        fun usingValue(value: Int): C2SPacketType {
            for (packetType in C2SPacketType.entries) {
                if (packetType.value == value) {
                    return packetType // 코드가 일치하는 enum 상수 반환
                }
            }
            error("value가 잘못되었습니다.")
        }
    }
}
