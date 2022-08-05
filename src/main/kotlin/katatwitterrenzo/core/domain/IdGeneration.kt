package katatwitterrenzo.core.domain.user

import java.nio.ByteBuffer
import java.util.*


object IdGenerator {
	private val encoder = Base64.getUrlEncoder()
	private const val bytesLength = 16
	private const val randomLength = 22

	fun newRandomId(): String {
		val uuid = UUID.randomUUID()!!

		val src = ByteBuffer.wrap(ByteArray(bytesLength))
			.putLong(uuid.mostSignificantBits)
			.putLong(uuid.leastSignificantBits)
			.array()

		return encoder.encodeToString(src).substring(0, randomLength)
	}
}
