package alt._7hereape.plugins

import com.aliucord.Logger

class Charset(vararg ranges: CharRange){
	val list: List<Char> = ranges.flatMap{it.toList()}.distinct().sorted();
	val min = 0;
	val max = list.lastIndex;

	operator fun get(i: Int): Char{
		if(i in min..max) return list[i]!!;
		return list[mod((i-min), (max-min+1))+min]!!;
	};
	fun char(string: StringBuilder, index: Int, offset: Int): Char{
		val codepoint = string[index]!!.toInt();
		return this[codepoint+offset].toChar();
	};
	fun index(string: StringBuilder, index: Int, offset: Int): Int{
		val char = string[index]!!;
		return this.indexOf(char)-offset;
	};
};
fun mod(x: Int, y: Int): Int{
	return ((x%y)+y)%y;
};