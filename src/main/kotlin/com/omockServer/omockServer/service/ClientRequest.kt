package com.omockServer.omockServer.service

data class ClientRequest(
    val session: ClientSession,
    val buffer: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientRequest

        if (session != other.session) return false
        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = session.hashCode()
        result = 31 * result + buffer.contentHashCode()
        return result
    }
}
