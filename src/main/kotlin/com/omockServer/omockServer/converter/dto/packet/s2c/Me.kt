package com.omockServer.omockServer.converter.dto.packet.s2c

import com.omockServer.omockServer.converter.dto.packet.s2c.Serializer.Companion.intToByteArray

class Me(
    val userId: Int,
) : S2CPayload() {
    override fun serialize(): ByteArray {
        val byteList = mutableListOf<Byte>()

        byteList.addAll(intToByteArray(userId).toList())

        return byteList.toByteArray()
    }
}
