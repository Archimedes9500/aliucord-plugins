package alt.archimedes5000.plugins;

import java.lang.reflect.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aliucord.utils.ReflectUtils;

import com.aliucord.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;

@AliucordPlugin
public class ReflectUtilsTest extends Plugin{

    public ReflectUtilsTest(){};

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

    public static Class<?> clazz = Test.class;
    public static String methodName = "test";
    public static Class<?>[] argTypes = {Integer.class};
    public static Integer number = 2;
    public static Object[] args = {number};

    public static int iterations = 1_000_000;

    public void noReflection() throws Throwable{
        for(int i = 0; i < iterations; i++){
            int result = Test.test(number);
        };
    };
    public void noCache() throws Throwable{
        for(int i = 0; i < iterations; i++){
            Method m = clazz.getDeclaredMethod(methodName, argTypes);
            m.setAccessible(true);
            int result = (int) m.invoke(null, number);
        };
    };
    public void withCache() throws Throwable{
        Method m = clazz.getDeclaredMethod(methodName, argTypes);
        m.setAccessible(true);
        for(int i = 0; i < iterations; i++){
            int result = (int) m.invoke(null, number);
        };
    };
    public void oldReflectUtils() throws Throwable{
        for(int i = 0; i < iterations; i++){
            Method m = oldGetMethodByArgs(clazz, methodName, args);
            int result = (int) m.invoke(null, number);
        };
    };
    public void oldReflectUtilsWithCache() throws Throwable{
        Method m = oldGetMethodByArgs(clazz, methodName, args);
        for(int i = 0; i < iterations; i++){
            int result = (int) m.invoke(null, number);
        };
    };
    public void newReflectUtils() throws Throwable{
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
        logger.debug("===== ===== TEST 1 ===== =====");
        time = System.nanoTime();
        noReflection();
        logger.debug("noReflection 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        noCache();
        logger.debug("noCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        withCache();
        logger.debug("withCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtils();
        logger.debug("oldReflectUtils 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtilsWithCache();
        logger.debug("oldReflectUtilsWithCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        newReflectUtils();
        logger.debug("newReflectUtils 1 000 000: "+(System.nanoTime()-time));

        //test 2
        logger.debug("===== ===== TEST 2 ===== =====");
        time = System.nanoTime();
        noReflection();
        logger.debug("noReflection 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        noCache();
        logger.debug("noCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        withCache();
        logger.debug("withCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtils();
        logger.debug("oldReflectUtils 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtilsWithCache();
        logger.debug("oldReflectUtilsWithCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        newReflectUtils();
        logger.debug("newReflectUtils 1 000 000: "+(System.nanoTime()-time));

        //test 3
        logger.debug("===== ===== TEST 3 ===== =====");
        time = System.nanoTime();
        noReflection();
        logger.debug("noReflection 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        noCache();
        logger.debug("noCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        withCache();
        logger.debug("withCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtils();
        logger.debug("oldReflectUtils 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        oldReflectUtilsWithCache();
        logger.debug("oldReflectUtilsWithCache 1 000 000: "+(System.nanoTime()-time));
        time = System.nanoTime();
        newReflectUtils();
        logger.debug("newReflectUtils 1 000 000: "+(System.nanoTime()-time));

    };

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    };
};