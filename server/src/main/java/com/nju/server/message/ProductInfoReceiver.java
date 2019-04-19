package com.nju.server.message;

import com.nju.server.utils.JsonUtil;
import com.product.common.ProductInfoOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductInfoReceiver {
    @RabbitListener(queuesToDeclare = @Queue("productInfo"))
    public void process(String message) {
        //把message转化成商品对象 ProductInfoOutput
        List<ProductInfoOutput> productInfoOutput = JsonUtil.fromJson(message, ProductInfoOutput.class);
        log.info("从队列{}接收到消息:{}", "productInfo", productInfoOutput);
    }
}
