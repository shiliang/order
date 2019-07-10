package com.nju.server.service.impl;

import com.nju.server.Entity.OrderDetail;
import com.nju.server.Entity.OrderMaster;
import com.nju.server.domain.impl.OrderCreated;
import com.nju.server.dto.OrderDTO;
import com.nju.server.enums.OrderStatusEnum;
import com.nju.server.enums.PayStatusEnum;
import com.nju.server.enums.ResultEnum;
import com.nju.server.exception.OrderException;
import com.nju.server.message.MqProvider;
import com.nju.server.repository.OrderDetailRepository;
import com.nju.server.repository.OrderMasterRepository;
import com.nju.server.service.OrderService;
import com.nju.server.utils.KeyUtil;
import com.product.client.ProductClient;
import com.product.common.DecreaseStockInput;
import com.product.common.ProductInfoOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private MqProvider mqProvider;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId = KeyUtil.genUniqueKey();

        //查询商品信息
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)   //把OrderDetail中getProductId属性取出来转化成list
                .collect(Collectors.toList());

        //用id号查看商品详情
        List<ProductInfoOutput> productInfoList = productClient.listForOrder(productIdList);

        //计算总价
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        //遍历每个订单，把商品数量*价格
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()
             ) {
            //单价*数量
            for (ProductInfoOutput productInfo: productInfoList
                 ) {
                if (productInfo.getProductId().equals(orderDetail.getProductId())) {
                    orderAmount = productInfo.getProductPrice()
                                .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                                .add(orderAmount);
                    BeanUtils.copyProperties(productInfo, orderDetail); //把productInfo相同的属性值拷贝到orderDetail
                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(KeyUtil.genUniqueKey());
                    //订单详情入库，单个商品
                    orderDetailRepository.save(orderDetail);
                }
            }
            
        }

        //扣库存(调用商品服务)
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))  //映射成另外一个元素
                .collect(Collectors.toList());  //转换数据结构

        //productClient.decreaseStock(decreaseStockInputList);
        //订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        Message<OrderCreated> message = new GenericMessage<>(new OrderCreated(orderMaster, decreaseStockInputList));

        //orderMasterRepository.save(orderMaster);
        mqProvider.sendTransactionMessage(message);

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(String orderId) {
        //1.先查询订单
        Optional<OrderMaster> orderMasterOptional = orderMasterRepository.findById(orderId);
        if (orderMasterOptional.isPresent()) {
            throw new OrderException(ResultEnum.CART_EMPTY);
        }
        //2.判断订单状态
        OrderMaster orderMaster = orderMasterOptional.get();
        if (OrderStatusEnum.NEW.getCode() != orderMaster.getOrderStatus()) {
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //3.修改订单状态为完结
        orderMaster.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        orderMasterRepository.save(orderMaster);

        //查询订单详情
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster, orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }
}
