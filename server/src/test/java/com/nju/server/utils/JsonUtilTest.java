package com.nju.server.utils;

import com.product.common.ProductInfoOutput;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class JsonUtilTest {

    @Test
    public void fromJson() {
        String message = "[{\"productId\":\"157875196366160022\",\"productName\":\"皮蛋粥\",\"productPrice\":0.01,\"productStock\":30,\"productDescription\":\"好吃的皮蛋粥\",\"productIcon\":\"//fuss10.elemecdn.com/0/49/65d10ef215d3c770ebb2b5ea962a7jpeg.jpeg\",\"productStatus\":0,\"categoryType\":1}]";
            List<ProductInfoOutput> productInfoOutput = JsonUtil.fromJson(message, ProductInfoOutput.class);
    }
}