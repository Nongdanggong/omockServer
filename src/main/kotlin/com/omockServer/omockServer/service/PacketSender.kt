package com.omockServer.omockServer.service

import com.omockServer.omockServer.OmockServerApplication.Companion.sessionMap
import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPacketType
import com.omockServer.omockServer.converter.dto.packet.s2c.S2CPayload
import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer

class PacketSender {
    val serializer: Serializer = Serializer()

    fun makePacket(
        packetType: S2CPacketType,
        data: S2CPayload? = null,
    ): ByteArray =
        serializer.serialize(
            packetType = packetType,
            target = data,
        )

    fun unicast(
        targetSession: ClientSession,
        packetType: S2CPacketType,
        data: S2CPayload? = null,
    ) {
        val outputStream = targetSession.socket.getOutputStream()
        val packet =
            makePacket(
                packetType = packetType,
                data = data,
            )
        outputStream.write(packet)
        println("[PacketSender - UNICAST] Session ${targetSession.id} 에게 ${packetType.name} 패킷 전송 완료")
    }

    fun multicast(
        targetSessionList: List<ClientSession>,
        packetType: S2CPacketType,
        data: S2CPayload? = null,
    ) {
        val packet =
            makePacket(
                packetType = packetType,
                data = data,
            )

        for (session in targetSessionList) {
            val outputStream = session.socket.getOutputStream()
            outputStream.write(packet)

            println("[PacketSender - MULTICAST] Session ${session.id} 에게 ${packetType.name} 패킷 전송 완료")
        }
    }

    fun broadcast(
        data: S2CPayload? = null,
        packetType: S2CPacketType,
    ) {
        // TODO thread safe 자료형에 socket 데이터들 저장해놓고 import 해와서 사용
        val packet =
            makePacket(
                packetType = packetType,
                data = data,
            )

        for (session in sessionMap.values) {
            val outputStream = session.socket.getOutputStream()
            outputStream.write(packet)

            println("[PacketSender - BROADCAST] Session ${session.id} 에게 ${packetType.name} 패킷 전송 완료")
        }
    }
}
