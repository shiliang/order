package com.nju.order.service.impl;

import com.nju.order.Entity.OrderMaster;
import com.nju.order.dto.OrderDTO;
import com.nju.order.repository.OrderDetailRepository;
import com.nju.order.repository.OrderMasterRepository;
import com.nju.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Override
    public OrderDTO create(OrderDTO orderDTO) {

        //订单入库
        OrderMaster orderMaster = new OrderMaster();

        return null;
    }
}
