package com.nju.server.controller;

import com.nju.server.VO.ResultVO;
import com.nju.server.converter.OrderForm2OrderDTOConverter;
import com.nju.server.dto.OrderDTO;
import com.nju.server.enums.ResultEnum;
import com.nju.server.exception.OrderException;
import com.nju.server.form.OrderForm;
import com.nju.server.message.MqProvider;
import com.nju.server.service.OrderService;
import com.nju.server.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 1. 参数检验
     * 2. 查询商品信息(调用商品服务)
     * 3. 计算总价
     * 4. 扣库存(调用商品服务)
     * 5. 订单入库
     */
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestBody OrderForm orderForm) {

        /*
            {"name": "张山", "phone":"123457","address":"上海","openid":"wedefref","items":"[{\"productId\":\"157875196366160022\",\"productQuantity\":2}]"}
         */
        // orderForm -> orderDTO
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车信息为空");
            throw new OrderException(ResultEnum.CART_EMPTY);
        }

        OrderDTO result = orderService.create(orderDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", result.getOrderId());
        return ResultVOUtil.success(map);
    }

    @PostMapping("/finish")
    public ResultVO<OrderDTO> finish(@RequestParam("orderId") String orderId) {
        return ResultVOUtil.success(orderService.finish(orderId));
    }



}
