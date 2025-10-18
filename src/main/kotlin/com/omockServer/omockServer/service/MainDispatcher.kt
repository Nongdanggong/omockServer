import com.omockServer.omockServer.OmockServerApplication.Companion.receiveQueue
import com.omockServer.omockServer.OmockServerApplication.Companion.sessionMap
import com.omockServer.omockServer.OmockServerApplication.Companion.userMap
import com.omockServer.omockServer.converter.dto.packet.c2s.C2SPacketType
import com.omockServer.omockServer.converter.dto.packet.c2s.DeSerializer
import com.omockServer.omockServer.converter.dto.packet.c2s.game.PlaceOnStone
import com.omockServer.omockServer.converter.dto.packet.c2s.game.StartGame
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.CreateRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.EnterRoom
import com.omockServer.omockServer.converter.dto.packet.c2s.gameroom.ExitRoom
import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPacketType
import com.omockServer.omockServer.converter.dto.packet.s2c.game.GameStatus
import com.omockServer.omockServer.converter.dto.packet.s2c.game.PlaceStoneResult
import com.omockServer.omockServer.converter.dto.packet.s2c.gameroom.GameRoomId
import com.omockServer.omockServer.converter.dto.packet.s2c.gameroom.GameRoomInformation
import com.omockServer.omockServer.converter.dto.packet.s2c.gameroom.IndividualGameRoom
import com.omockServer.omockServer.converter.dto.packet.s2c.gameroom.ModifyNumberOfUser
import com.omockServer.omockServer.service.ClientRequest
import com.omockServer.omockServer.service.PacketSender
import com.omockServer.omockServer.service.game.GameManager
import com.omockServer.omockServer.service.gameRoom.GameRoom
import com.omockServer.omockServer.service.gameRoom.GameRoomManager
import com.omockServer.omockServer.service.lobby.LobbyManager
import kotlin.collections.set

class MainDispatcher : Thread() {
    private val gameRoomManager: GameRoomManager = GameRoomManager()
    private val gameManager: GameManager = GameManager()
    private val lobbyManager: LobbyManager = LobbyManager()
    private val deserializer: DeSerializer = DeSerializer()
    private val packetSender: PacketSender = PacketSender()
    // TODO SessionManager

    override fun run() {
        val defaultLobbyId = 0

        while (true) {
            val clientRequest: ClientRequest = receiveQueue.take()

            val c2sPacket = deserializer.deserialize(clientRequest.buffer)

            val sessionId = clientRequest.session.id
            println("[Dispatcher] Session ID $sessionId 로부터 ${c2sPacket.packetType.name} 패킷 수신")

            try {
                when (c2sPacket.packetType) {
                    C2SPacketType.GET_ROOM_LIST -> {
                        val gameLoomList = gameRoomManager.getRoomList()
                        packetSender.unicast(
                            targetSession = clientRequest.session,
                            packetType = S2CPacketType.ROOM_LIST,
                            data = gameLoomList,
                        )
                    }

                    C2SPacketType.ROOM_CREATE -> {
                        val payload = c2sPacket.payload!! as CreateRoom

                        val createdRoom = gameRoomManager.createRoom(userMap[sessionId]!!, roomName = payload.roomName)

                        lobbyManager.exit(user = userMap[sessionId]!!)

                        // TODO 1 패킷 데이터 최소한으로 수정 필요
                        // 방 목록 업데이트
//                    val gameLoomList = gameRoomManager.getRoomList()
//                    packetSender.broadcast(
//                        packetType = S2CPacketType.ROOM_LIST,
//                        data = gameLoomList,
//                    )

//                    // TODO 2 대기실에 있는 유저에게만 보내도록 수정
//                    packetSender.broadcast(
//                        packetType = S2CPacketType.ROOM_CREATED,
//                        data = IndividualGameRoom(room = createdRoom),
//                    )

                        val sessionListInLobby =
                            lobbyManager.lobbyMap[defaultLobbyId]!!.userList.map {
                                sessionMap[it.userId]!!
                            }

                        packetSender.multicast(
                            targetSessionList = sessionListInLobby,
                            packetType = S2CPacketType.ROOM_CREATED,
                            data = IndividualGameRoom(room = createdRoom),
                        )

                        // 방 정보 반환
                        val gameRoomInformation =
                            GameRoomInformation(
                                gameRoom = createdRoom,
                            )

                        packetSender.unicast(
                            targetSession = clientRequest.session,
                            packetType = S2CPacketType.GAME_ROOM_INFORMATION,
                            data = gameRoomInformation,
                        )
                    }

                    C2SPacketType.ROOM_ENTER -> {
                        val payload = c2sPacket.payload!! as EnterRoom

                        lobbyManager.exit(user = userMap[sessionId]!!)

                        val targetRoom: GameRoom?

                        // error check
                        if (!gameRoomManager.isExistRoom(payload.roomId)) {
                            packetSender.unicast(
                                targetSession = clientRequest.session,
                                packetType = S2CPacketType.ERROR_ROOM_NOT_EXIST,
                            )
                        } else {
                            val targetRoom = gameRoomManager.roomMap[payload.roomId]!!
                            if (!targetRoom.isPassibleToEnter()) {
                                packetSender.unicast(
                                    targetSession = clientRequest.session,
                                    packetType = S2CPacketType.ERROR_ROOM_IS_FULL,
                                )
                            } else {
                                gameRoomManager.enterRoom(payload.roomId, userMap[sessionId]!!)

                                val gameRoomInformation =
                                    GameRoomInformation(
                                        gameRoom = targetRoom,
                                    )

                                packetSender.multicast(
                                    targetSessionList = targetRoom.userList.map { it -> sessionMap[it.userId]!! },
                                    packetType = S2CPacketType.GAME_ROOM_INFORMATION,
                                    data = gameRoomInformation,
                                )

                                val sessionListInLobby =
                                    lobbyManager.lobbyMap[defaultLobbyId]!!.userList.map {
                                        sessionMap[it.userId]!!
                                    }

                                packetSender.multicast(
                                    targetSessionList = sessionListInLobby,
                                    packetType = S2CPacketType.ROOM_NUMBER_OF_USER_MODIFIED,
                                    data = ModifyNumberOfUser(gameRoom = targetRoom),
                                )
                            }
                        }
                    }

                    C2SPacketType.ROOM_EXIT -> {
                        val payload = c2sPacket.payload!! as ExitRoom

                        lobbyManager.enter(user = userMap[sessionId]!!)

                        gameRoomManager.exitRoom(payload.roomId, userMap[sessionId]!!)

                        packetSender.unicast(
                            targetSession = clientRequest.session,
                            packetType = S2CPacketType.ROOM_EXIT_OK,
                        )

                        // 방에 남아있는 유저들에게 방 정보 업데이트
                        if (gameRoomManager.isExistRoom(payload.roomId)) {
                            val targetRoom = gameRoomManager.roomMap[payload.roomId]!!

                            val gameRoomInformation =
                                GameRoomInformation(
                                    gameRoom = targetRoom,
                                )

                            packetSender.multicast(
                                targetSessionList = targetRoom.userList.map { it -> sessionMap[it.userId]!! },
                                packetType = S2CPacketType.GAME_ROOM_INFORMATION,
                                data = gameRoomInformation,
                            )

                            val sessionListInLobby =
                                lobbyManager.lobbyMap[defaultLobbyId]!!.userList.map {
                                    sessionMap[it.userId]!!
                                }

                            packetSender.multicast(
                                targetSessionList = sessionListInLobby,
                                packetType = S2CPacketType.ROOM_NUMBER_OF_USER_MODIFIED,
                                data = ModifyNumberOfUser(gameRoom = targetRoom),
                            )
                        } else {
                            val sessionListInLobby =
                                lobbyManager.lobbyMap[defaultLobbyId]!!.userList.map {
                                    sessionMap[it.userId]!!
                                }

                            packetSender.multicast(
                                targetSessionList = sessionListInLobby,
                                packetType = S2CPacketType.ROOM_REMOVED,
                                data = GameRoomId(roomId = payload.roomId),
                            )
                        }
                    }

                    C2SPacketType.START_GAME -> {
                        val payload = c2sPacket.payload!! as StartGame

                        val roomId = payload.roomId
                        val room = gameRoomManager.roomMap[roomId]!!

                        val userList = room.userList

                        // TODO: user가 2명이 아닐 때 에러처리

                        val gameStartSetting =
                            gameManager.startNewGame(
                                gameRoomId = payload.roomId,
                                firstUser = userList[0],
                                secondUser = userList[1],
                            )

                        packetSender.multicast(
                            targetSessionList = room.userList.map { it -> sessionMap[it.userId]!! },
                            packetType = S2CPacketType.GAME_START_SETTING,
                            data = gameStartSetting,
                        )
                    }

                    C2SPacketType.PLACE_ON_STONE -> {
                        val payload = c2sPacket.payload!! as PlaceOnStone

                        val game = gameManager.gameMap[payload.gameId]!!

                        val currentTurn = gameManager.getNowTurnPlayerId(payload.gameId)

                        val isWin =
                            gameManager.placeStoneAndCheckWin(
                                gameRoomId = payload.gameId,
                                playerId = payload.playerId,
                                x = payload.x,
                                y = payload.y,
                            )

                        val targetSessionList =
                            listOf(
                                sessionMap[game.firstPlayer.user.userId]!!,
                                sessionMap[game.secondPlayer.user.userId]!!,
                            )

                        val placeStoneResult: PlaceStoneResult? =
                            if (isWin) {
                                PlaceStoneResult(
                                    currentTurnPlayerId = currentTurn,
                                    x = payload.x,
                                    y = payload.y,
                                    nextTurnPlayerId = gameManager.getNowTurnPlayerId(payload.gameId),
                                    gameStatus = GameStatus.WIN,
                                    winnerPlayerId = payload.playerId,
                                )
                            } else {
                                PlaceStoneResult(
                                    currentTurnPlayerId = currentTurn,
                                    x = payload.x,
                                    y = payload.y,
                                    nextTurnPlayerId = gameManager.getNowTurnPlayerId(payload.gameId),
                                    gameStatus = GameStatus.ONGOING,
                                )
                            }

                        packetSender.multicast(
                            targetSessionList = targetSessionList,
                            packetType = S2CPacketType.PLACE_STONE_RESULT,
                            data = placeStoneResult,
                        )
                    }

                    C2SPacketType.ENTER_LOBBY -> {
                        lobbyManager.enter(user = userMap[sessionId]!!)

                        packetSender.unicast(
                            targetSession = clientRequest.session,
                            packetType = S2CPacketType.ENTER_LOBBY_OK,
                        )
                    }

                    C2SPacketType.CLOSE_CONNECTION -> {
                        // 봇에선 close connection 시에 무조건 로비에 있기 때문에 로비만 제거하면 되지만
                        // 게임 방에 있다면 나감 처리
                        // 게임 중이라면 .. 상대방 승리 처리 + 게임 삭제 하는 등등 로직 필요
                        // 연결 종료 로직을 따로 빼서 처리하고 연결이 강제로 끊겼을 때도 돌려야 할듯
                        lobbyManager.exit(user = userMap[sessionId]!!)

                        packetSender.unicast(
                            targetSession = clientRequest.session,
                            packetType = S2CPacketType.CONNECTION_CLOSED_OK,
                        )

                        sessionMap.remove(sessionId)
                        userMap.remove(sessionId)
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                println(
                    "MainDispatcher Exception 발생. sessionId: $sessionId, packetType: ${c2sPacket.packetType}, error: $e, errorMessage: ${e.message}",
                )
                throw (e)
            }
        }
    }
}
