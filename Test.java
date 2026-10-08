import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("net.minecraft.world.item.trading.Merchant");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m.getReturnType().getSimpleName() + " " + m.getName() + "()");
        }
    }
}
