package com.nju.server.service;

import com.nju.server.dto.OrderDTO;

public interface OrderService {
    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    OrderDTO create(OrderDTO orderDTO);

    //完结订单,只能卖家操作
    OrderDTO finish(String orderId);
}
