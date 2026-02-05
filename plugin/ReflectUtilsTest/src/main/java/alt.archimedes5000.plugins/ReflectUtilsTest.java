package alt.archumedes5000.plugins;

import java.util.Random;
import java.lang.reflect.*;
import com.aliucord.utils.ReflectUtils;

import com.aliucord.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;

@AliucordPlugin
public class ReflectUtilsTest extends Plugin{

    public ReflectUtilsTest(){};

    public Random random = new Random();

    @NonNull
    public Method oldGetMethodByArgs(@NonNull Class<?> clazz, @NonNull String methodName, Object... args) throws NoSuchMethodException {
        Class<?>[] argTypes = null;
        if (args != null) {
            argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
        }
    
        Method m = clazz.getDeclaredMethod(methodName, argTypes);
        m.setAccessible(true);
        return m;
    }

    public static Class<?> clazz = ReflectUtilsTest.class;
    public static String methodName = "test";
    public static Class<?>[] argTypes = {int.class};
    public static int number = 2;
    public static Object[] args = {number};

    private static int test(int number){
        int result = random.nextInt();
        result = result/number;
        return result;
    };

    public static int iterations = 1_000_000;

    public void noReflection(){
        for(int i = 0; i < iterations; i++){
            int result = test(number);
        };
    };
    public void noCache(){
        for(int i = 0; i < iterations; i++){
            Method m = clazz.getDeclaredMethod(methodName, argTypes);
            m.setAccessible(true);
            int result = (int) m.invoke(null, number);
        };
    };
    public void withCache(){
        Method m = clazz.getDeclaredMethod(methodName, argTypes);
        m.setAccessible(true);
        for(int i = 0; i < iterations; i++){
            int result = (int) m.invoke(null, number);
        };
    };
    public void oldReflectUtils(){
        for(int i = 0; i < iterations; i++){
            Method m = oldGetMethodByArgs(clazz, methodName, args);
            int result = (int) m.invoke(null, number);
        };
    };
    public void oldReflectUtilsWithCache(){
        Method m = oldGetMethodByArgs(clazz, methodName, args);
        for(int i = 0; i < iterations; i++){
            int result = (int) m.invoke(null, number);
        };
    };
    public void newReflectUtils(){
        for(int i = 0; i < iterations; i++){
            Method m = ReflectUtils.getMethodByArgs(clazz, methodName, args);
            int result = (int) m.invoke(null, number);
        };
    };

    @Override
    public void start(Context context) throws Throwable{
        long time;

        //warmup
        noReflection();
        noCache();
        withCache();
        oldReflectUtils();
        oldReflectUtilsWithCache();
        newReflectUtils();

        //test 1
        time = System.nanoTime();
        noReflection();
        logger.debug("1 noReflection 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        noCache();
        logger.debug("1 noCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        withCache();
        logger.debug("1 withCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtils();
        logger.debug("1 oldReflectUtils 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtilsWithCache();
        logger.debug("1 oldReflectUtilsWithCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        newReflectUtils();
        logger.debug("1 newReflectUtils 1 000 000: "+(System.nanoTime()-time));

        //test 2
        time = System.nanoTime();
        noReflection();
        logger.debug("2 noReflection 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        noCache();
        logger.debug("2 noCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        withCache();
        logger.debug("2 withCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtils();
        logger.debug("2 oldReflectUtils 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtilsWithCache();
        logger.debug("2 oldReflectUtilsWithCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        newReflectUtils();
        logger.debug("2 newReflectUtils 1 000 000: "+(System.nanoTime()-time));
    };

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    };
};