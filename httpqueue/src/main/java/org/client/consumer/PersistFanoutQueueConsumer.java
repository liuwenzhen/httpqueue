package org.client.consumer;

import org.client.consumer.intf.Consume;
import org.client.consumer.intf.IConsumer;
import org.client.publisher.util.config.Config;

/**
 * Created by andilyliao on 16-4-7.
 */
public class PersistFanoutQueueConsumer extends Consume implements IConsumer {
    @Override
    public void initPublisher(Config config) throws Exception {

    }
}
