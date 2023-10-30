package botapi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ControllerContextHandler implements InvocationHandler {

    private final ControllerContext controllerContext;
    private final Logger logger = Logger.getLogger(ControllerContextHandler.class.getName());

    public ControllerContextHandler(ControllerContext controllerContext) {
        this.controllerContext = controllerContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("Called Method " + method.getName());
        return method.invoke(controllerContext, args);
    }
}
