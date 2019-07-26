package com.nju.server.message;

import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MqProvider {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public boolean send(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
        System.out.println("发送消息:" + msg);
        return true;
    }

    public boolean sendTransactionMessage(Message message) {
        TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction("txOrder","order-trans", message, null);
        System.out.println("发送事务消息:" + message);
        return true;
    }
}
