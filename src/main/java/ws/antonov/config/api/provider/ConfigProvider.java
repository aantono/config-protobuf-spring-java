package ws.antonov.config.api.provider;

import com.google.protobuf.Message;
import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public interface ConfigProvider {
    public Message.Builder retrieveConfigData(Class<? extends Message> configClass,
                                              ConfigParamsBuilder.ConfigParamsMap configParams) throws IOException;
}
