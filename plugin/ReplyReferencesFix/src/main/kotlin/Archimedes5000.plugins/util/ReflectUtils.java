package Archimedes5000.plugins.util;
import java.lang.reflect.*;
import Archimedes5000.plugins.util.ExistingProp;
class ReflectUtils{
	public static ExistingProp propRef(Class<?> c, String name, Class<?>... classes){
		Object prop;
		if(classes.length == 0){
			try{
				prop = c.getDeclaredField(name);
				return new ExistingProp((Field)prop);
			}catch(NoSuchFieldException e){
				try{
					prop = c.getDeclaredMethod(name);
					return new ExistingProp((Method)prop);
				}catch(NoSuchMethodException ee){
					ee.printStackTrace();
					return null;
				}
			}
		}else{
			try{
				prop = c.getDeclaredMethod(name, classes);
				return new ExistingProp((Method)prop);
			}catch(NoSuchMethodException e){
				e.printStackTrace();
				return null;
			}
		}
	}
}