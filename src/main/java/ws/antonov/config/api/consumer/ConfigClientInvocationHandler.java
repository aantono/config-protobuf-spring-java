package ws.antonov.config.api.consumer;

import com.google.protobuf.Message;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public class ConfigClientInvocationHandler implements InvocationHandler {
    private ConfigClient configClient;
    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public ConfigClientInvocationHandler(ConfigClient configClient) {
        this.configClient = configClient;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        List<ConfigParamsBuilder.ConfigParamEntry> configParams = generateConfigParams(method, objects);
        Assert.isAssignable(Message.class, method.getReturnType());
        return configClient.getConfig((Class<Message>) method.getReturnType(), configParams);
    }

    private List<ConfigParamsBuilder.ConfigParamEntry> generateConfigParams(Method method,
                                                                            Object[] args) {
        ConfigParam[] configParamAnnotations = retrieveRequestParams(method);
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        ConfigParamsBuilder configParamsBuilder = new ConfigParamsBuilder();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            ConfigParam configParam = configParamAnnotations[i];
            String paramName;
            if (parameterNames != null && (configParam == null || configParam.value() == "")) {
                paramName = parameterNames[i];
                configParamsBuilder.addParam(paramName, arg);
            } else if (configParam != null && configParam.value().length() > 0) {
                paramName = configParam.value();
                configParamsBuilder.addParam(paramName, arg);
            }
        }
        return configParamsBuilder.build();
    }

    public static ConfigParam[] retrieveRequestParams(Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        ConfigParam[] paramNames = new ConfigParam[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] anns = annotations[i];
            paramNames[i] = null;
            for (Annotation ann : anns) {
                if (ConfigParam.class.isInstance(ann)) {
                    paramNames[i] = (ConfigParam) ann;
                    break;
                }
            }
        }
        return paramNames;
    }
}
