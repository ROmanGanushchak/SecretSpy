package GameController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationHandlerGameContrl implements InvocationHandler {
    private final Object target;

    public InvocationHandlerGameContrl(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Entering method: " + method.getName() + ", with arguments: " + java.util.Arrays.toString(args));
        Object result = method.invoke(target, args);

        return result;
    }
}
