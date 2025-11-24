package alt._7hereape.plugins

class Key(vararg args: Int){
	val key: IntArray = intArrayOf(*args);
	val min = 0;
	val max = key.lastIndex();

	operator fun get(i: Int): Int{
		return key[mod((i-min), (max-min+1))+min]!!;
	};
};
fun mod(x: Int, y: Int): Int{
	return ((x%y)+y)%y;
};