package org.testconsume;

import org.client.consumer.QueueConsumer;
import org.client.consumer.intf.IConsumer;
import org.client.consumer.util.config.Config;
import org.client.consumer.util.queueconfig.QueueConfig;
import org.client.consumer.util.result.CommonRes;
import org.client.consumer.util.result.MsgRes;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by andilyliao on 16-4-2.
 */
public class ConsumeTest {
    @Test
    public void testconsume() throws Exception {
        Config config=new Config("/consumer.properties");
        IConsumer iConsumer=new QueueConsumer(config);
        QueueConfig queueConfig=new QueueConfig();
        queueConfig.setQueueName("aaa");
        CommonRes commonRes = iConsumer.registConsumer(queueConfig);
        MsgRes msgRes=new MsgRes();
        while(true) {
            msgRes = iConsumer.consumeMsg(msgRes);//消费一条
        }
    }
}
