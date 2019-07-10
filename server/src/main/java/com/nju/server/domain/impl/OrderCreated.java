package com.nju.server.domain.impl;

import com.nju.server.Entity.OrderMaster;
import com.nju.server.domain.OrderDomainEvent;
import com.product.common.DecreaseStockInput;
import lombok.Data;

import java.util.List;


@Data
public class OrderCreated implements OrderDomainEvent {
    private OrderMaster orderMaster;
    private List<DecreaseStockInput> decreaseStockInputs;

    public OrderCreated(OrderMaster orderMaster, List<DecreaseStockInput> decreaseStockInputs) {
        this.orderMaster = orderMaster;
        this.decreaseStockInputs = decreaseStockInputs;
    }
}
