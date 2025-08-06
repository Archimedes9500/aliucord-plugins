package com.aliucord.util;
import java.lang.reflect.*;
public class ExistingProp{
	public Object prop;
	public ExistingProp(Field field){
		this.prop = field;
	}
	public ExistingProp(Method method){
		this.prop = method;
	}
	public Object get(Object instance){
		Object o = null;
		if(this.prop instanceof Field){
			try{
				Field field = (Field)this.prop;
				o = field.get(instance);
			}catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}else{
			//throw ThisShitIsNotAFieldException;
		}
		return o;
	}
	public Object invoke(Object instance, Object... args){
		Object o = null;
		if(this.prop instanceof Method){
			try{
				Method method = (Method)this.prop;
				o = method.invoke(instance, args);
			}catch(InvocationTargetException e){
				e.printStackTrace();
			}catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}else{
			//throw ThisShitIsNotAMethodException;
		}
		return o;
	}
}

import java.lang.reflect.*;
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