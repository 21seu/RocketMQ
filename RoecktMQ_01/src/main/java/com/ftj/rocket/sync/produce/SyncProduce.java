package com.ftj.rocket.sync.produce;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * Created by fengtj on 2022/5/16 7:55
 */
public class SyncProduce {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        //1、创建消息生产这produce，并且指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        //指定nameserver
        producer.setNamesrvAddr("127.0.0.1:9876");
        //3、启动produce
        producer.start();
        //创建消息对象，指定主题topic tag和消息体
        for (int i = 0; i < 10; i++) {
            /**
             * 参数1：消息主题topic
             * 参数2：消息的tag
             * 参数3：消息内容
             */
            Message message = new Message("sync", "tag1", ("hello world" + i).getBytes());
            //5、发送消息
            SendResult result = producer.send(message);
            System.out.println(result);

            TimeUnit.SECONDS.sleep(1);
        }
        //关闭生产者produce
        producer.shutdown();
    }
}
