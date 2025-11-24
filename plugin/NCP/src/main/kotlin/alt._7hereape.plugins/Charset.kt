package alt._7hereape.plugins

class Charset(vararg ranges: IntRange){
	val list: List<Int> = ranges.flatMap{it.toList()}.distinct().sorted();
	val min = 0;
	val max = list.lastIndex;

	operator fun get(i: Int): Int{
		return list[mod((i-min), (max-min+1))+min]!!;
	};
	fun char(string: StringBuilder, index: Int, offset: Int): Char{
		val codepoint = string[index]!!.toInt();
		return this[codepoint+offset].toChar();
	};
};
fun mod(x: Int, y: Int): Int{
	return ((x%y)+y)%y;
};