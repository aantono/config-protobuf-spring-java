package ws.antonov.config.api.consumer;

import com.google.protobuf.Message;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Copyright 2011 Alex Antonov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * @author aantonov
 * @since 0.1
 */
public class ConfigClientInvocationHandler implements InvocationHandler {
    private ConfigClient configClient;
    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public ConfigClientInvocationHandler(ConfigClient configClient) {
        this.configClient = configClient;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        ConfigParamsBuilder.ConfigParamsMap configParams = generateConfigParams(method, objects);
        Assert.isAssignable(Message.class, method.getReturnType());
        return configClient.getConfig((Class<Message>) method.getReturnType(), configParams);
    }

    private ConfigParamsBuilder.ConfigParamsMap generateConfigParams(Method method,
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
