package com.nju.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 转换为json字符串
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //json字符串转成对象
    public static <T> List<T> fromJson(String json,
                                             Class<T> classOnWhichArrayIsDefined) {
        try {
            Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + classOnWhichArrayIsDefined.getName() + ";");

            T[] objects = objectMapper.readValue(json, arrayClass);
            return Arrays.asList(objects);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

}
