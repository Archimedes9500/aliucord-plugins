package alt._7hereape.plugins

class Encryption(
	val key: IntArray,
	val charset: Charset
){
	lateinit var offset: (i: Int) -> Int;
};