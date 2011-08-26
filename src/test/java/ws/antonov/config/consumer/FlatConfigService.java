package ws.antonov.config.consumer;

import ws.antonov.config.api.consumer.ConfigParam;
import ws.antonov.config.test.proto.model.FlatConfigObject;

/**
 */
public interface FlatConfigService {
    public FlatConfigObject getConfig(@ConfigParam("domain") String domain,
                                      @ConfigParam("path") String path);
}
