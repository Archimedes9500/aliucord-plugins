package alt.archimedes5000.util;
import java.lang.reflect.*;
public class ExistingProp{
	public Object prop;
	public ExistingProp(Field field){
		field.setAccessible(true);
		this.prop = field;
	}
	public ExistingProp(Method method){
		method.setAccessible(true);
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