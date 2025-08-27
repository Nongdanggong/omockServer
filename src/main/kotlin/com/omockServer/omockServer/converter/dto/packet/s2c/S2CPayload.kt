package com.omockServer.omockServer.converter.dto.packet.s2c

abstract class S2CPayload {
    abstract fun serialize(): ByteArray
}
