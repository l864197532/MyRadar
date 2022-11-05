package com.xqq.myradar.kafka.config;


import com.xqq.myradar.kafka.listener.MessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({radarKafkaConfig.class,PicKafkaConfig.class,DFKafkaConfig.class,FPKafkaConfig.class})
public class kafkaConfig {
    @Bean
    public MessageListener kafkaConsumer(){
        MessageListener consumer = new MessageListener();
        return consumer;
    }
}
