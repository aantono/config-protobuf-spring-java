package ws.antonov.config.api.provider;

import ws.antonov.config.api.consumer.ConfigParamsBuilder;

import java.io.IOException;
import java.util.List;

import com.google.protobuf.Message;

/**
 * @author aantonov
 * Created On Oct 20, 2010
 */
public interface ConfigProvider {
    public Message.Builder retrieveConfigData(Class<? extends Message> configClass,
                                              List<ConfigParamsBuilder.ConfigParamEntry> configParams) throws IOException;
}
