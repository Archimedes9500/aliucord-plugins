package alt._7hereape.plugins

class Encryption(
	val key: IntArray,
	val charset: Charset,
	val offset: (i: Int) -> Int
){};