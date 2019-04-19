package com.nju.server.service.impl;

import com.nju.server.Entity.OrderDetail;
import com.nju.server.Entity.OrderMaster;
import com.nju.server.Entity.ProductInfo;
import com.nju.server.dto.CartDTO;
import com.nju.server.dto.OrderDTO;
import com.nju.server.enums.OrderStatusEnum;
import com.nju.server.enums.PayStatusEnum;
import com.nju.server.repository.OrderDetailRepository;
import com.nju.server.repository.OrderMasterRepository;
import com.nju.server.service.OrderService;
import com.nju.server.utils.KeyUtil;
import com.product.client.ProductClient;
import com.product.common.DecreaseStockInput;
import com.product.common.ProductInfoOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private ProductClient productClient;

    @Override
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
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockInputList);

        //订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }
}
