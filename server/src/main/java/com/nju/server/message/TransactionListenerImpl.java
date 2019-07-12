package com.nju.server.message;


import com.nju.server.Entity.OrderMaster;
import com.nju.server.domain.DomainEvent;
import com.nju.server.domain.OrderDomainEvent;
import com.nju.server.domain.impl.OrderCreated;
import com.nju.server.repository.OrderMasterRepository;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import java.util.concurrent.ConcurrentHashMap;

@RocketMQTransactionListener(txProducerGroup = "txOrder")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    //存储本地事务的执行结果，方便后面回查的时候
    private ConcurrentHashMap<String, RocketMQLocalTransactionState> localTrans = new ConcurrentHashMap<>();

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        //执行本地事务
        OrderCreated event = (OrderCreated) message.getPayload();
        String transId = (String) message.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        try {
            OrderMaster orderMaster = event.getOrderMaster();
            orderMasterRepository.save(orderMaster);
            localTrans.put(transId, RocketMQLocalTransactionState.COMMIT);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }


    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        //回查本地事务的执行状态,未收到Commit或者Rollback消息执行回查
        /*
            消息回查：由于网络闪断、生产者应用重启等原因，导致某条事务消息的二次确认丢失，
            消息队列 RocketMQ 服务端通过扫描发现某条消息长期处于“半消息”时，
            需要主动向消息生产者询问该消息的最终状态（Commit 或是 Rollback），该过程即消息回查。
         */
        String transId = (String) message.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        RocketMQLocalTransactionState state = localTrans.get(transId);
        return state;
    }
}
