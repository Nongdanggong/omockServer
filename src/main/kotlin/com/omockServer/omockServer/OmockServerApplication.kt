package com.omockServer.omockServer

import MainDispatcher
import com.omockServer.omockServer.converter.dto.packet.c2s.DeSerializer
import com.omockServer.omockServer.converter.dto.packet.s2c.Me
import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPacketType
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer
import com.omockServer.omockServer.service.ClientRequest
import com.omockServer.omockServer.service.ClientSession
import com.omockServer.omockServer.service.User
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

// @SpringBootApplication
// class OmockServerApplication

// TODO: Map 은 thread safe 한가?
// val sessionMap = mutableMapOf<Int, ClientSession>()
// val receiveQueue: BlockingQueue<ClientRequest> = LinkedBlockingQueue(100)
//
// val userMap: MutableMap<Int, User> = mutableMapOf()

class OmockServerApplication {
    companion object {
        // @JvmStatic // Java와 함께 사용하는 경우를 위해 추가하면 좋음
        val sessionMap = mutableMapOf<Int, ClientSession>()
        val receiveQueue: BlockingQueue<ClientRequest> = LinkedBlockingQueue(100)
        val userMap: MutableMap<Int, User> = mutableMapOf()
    }
}

val serializer = Serializer()
val deserializer = DeSerializer()

class ServerThread(
    val socketId: Int,
    val client: Socket,
) : Thread() {
    val session = ClientSession(id = socketId, socket = client)

    override fun run() {
        val buffer = ByteArray(1024)
        // 클라이언트로부터의 요청을 읽기 위한 입력 스트림 설정
        val inputStream = client.getInputStream()
        // 클라이언트에게 응답을 보내기 위한 출력 스트림 설정
        val outputStream = client.getOutputStream()

//         최초 접속 시 Id 반환
        val me = Me(userId = socketId)
        outputStream.write(
            serializer.serialize(
                packetType = S2CPacketType.GET_ME,
                target = me,
            ),
        )

        while (true) {
            try {
                val readSize = inputStream.read(buffer)

                println("[IOThread] Session ID ${socketId}에게 패킷 받음, 크기: $readSize")

                if (readSize == -1) {
                    // readSize == -1 일 때
                    println("\n[IOThread] Session ID ${socketId}가 정상적으로 연결을 종료했습니다.")
                    client.close()
                    break
                }

                val frameList: List<ByteArray> = deserializer.getFrameList(buffer.copyOf(readSize))

                for (frame in frameList) {
                    val clientRequest =
                        ClientRequest(
                            session = session,
                            buffer = frame,
                        )
                    OmockServerApplication.receiveQueue.put(clientRequest)
                }
            } catch (e: Exception) {
                println("\n[알림] ${socketId}번째 소켓의 연결이 비정상적으로 끊겼습니다.")
                client.close()
                break
            }
        }

        OmockServerApplication.sessionMap.remove(socketId)
    }
}

fun main(args: Array<String>) {
// 	runApplication<OmockServerApplication>(*args)
    val serverSocket = ServerSocket(8000)

//    serverSocket// TCP no-delay

    var clientId = 0

    val mainDispatcher = MainDispatcher()
    mainDispatcher.start()

    while (true) {
        println("현재 연결된 클라이언트 개수: ${OmockServerApplication.sessionMap.size}")
        val client: Socket = serverSocket.accept()
        clientId += 1
        println("[알림] $clientId 번째 소켓이 연결되었습니다!")

        client.tcpNoDelay = true

        OmockServerApplication.sessionMap[clientId] = ClientSession(clientId, client)
        OmockServerApplication.userMap[clientId] = User(userId = clientId)

        val echoServerThread = ServerThread(clientId, client)
        echoServerThread.start()
    }
}
