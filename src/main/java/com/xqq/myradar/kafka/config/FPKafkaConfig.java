package com.xqq.myradar.kafka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public class FPKafkaConfig {
 
    @Bean
    public KafkaListenerContainerFactory fpKafkaFactory(@Autowired @Qualifier("fpConsumerFactory") ConsumerFactory fpConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(fpConsumerFactory);
        factory.setConcurrency(10);
        factory.setBatchListener(true);
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//手动提交
 
        return factory;
    }
 
    @Bean
    public ConsumerFactory fpConsumerFactory(@Autowired @Qualifier("fpKafkaProperties") KafkaProperties fpKafkaProperties){
        return new DefaultKafkaConsumerFactory(fpKafkaProperties.buildConsumerProperties());
    }
 
 
    @ConfigurationProperties(prefix = "sys.kafka.fp")
    @Bean
    public KafkaProperties fpKafkaProperties(){
        return new KafkaProperties();
    }
 
}