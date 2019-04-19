package com.nju.server.message;

import com.nju.server.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(StreamClient.class)
@Slf4j
public class StreamReceiver {

    @StreamListener(StreamClient.INPUT)
    public void processInput(OrderDTO message) {
        log.info("StreamReceiver: {}", message);
    }

    @StreamListener("myMessageOutput")
    public void processOutput(Object message) {
        log.info("StreamReceiver: {}", message);
    }
}
