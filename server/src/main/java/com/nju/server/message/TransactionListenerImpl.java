package com.nju.server.message;


import com.nju.server.domain.impl.OrderCreated;
import com.nju.server.repository.OrderMasterRepository;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

@RocketMQTransactionListener(txProducerGroup = "txOrder")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        //执行本地事务

        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        //回查本地事务的执行状态
        return null;
    }
}
