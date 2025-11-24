package alt._7hereape.plugins

class Encryption(
	val key: IntArray,
	val charset: Charset,
	val offset: (i: Int) -> Int
){
	fun component1(): Int{
		return this.key;
	};
	fun component2(): Int{
		return this.ranges;
	};
	fun component3(): Int{
		return this.next;
	};
};