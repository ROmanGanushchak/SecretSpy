package GameController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/** class to create a dinamic proxy */
public class InvocationHandlerGameContrl implements InvocationHandler {
    /** obj for whitch the proxy will be created */
    private final Object target;

    /**constractor
     * @param target the obj for whitch the proxy will be created
     */
    public InvocationHandlerGameContrl(Object target) {
        this.target = target;
    }

    /** the method execution */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Entering method: " + method.getName() + ", with arguments: " + java.util.Arrays.toString(args));
        Object result = method.invoke(target, args);

        return result;
    }
}
