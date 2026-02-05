package alt.archimedes5000.plugins;

import java.util.Random;

public class Test{
    public static Random random = new Random();

    private static int test(int number){
        int result = random.nextInt();
        result = result/number;
        return result;
    };
};