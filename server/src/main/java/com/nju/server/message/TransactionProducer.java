package com.nju.server.message;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionProducer {
    @Autowired
    RocketMQTemplate rocketMQTemplate;



}
