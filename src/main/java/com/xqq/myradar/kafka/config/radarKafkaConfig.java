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


public class radarKafkaConfig {
    @Bean
    public KafkaListenerContainerFactory radarKafkaFactory(@Autowired @Qualifier("radarConsumerFactory") ConsumerFactory radarConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(radarConsumerFactory);
        factory.setConcurrency(10);
        factory.setBatchListener(true);
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//手动提交

        return factory;
    }

    @Bean
    public ConsumerFactory radarConsumerFactory(@Autowired @Qualifier("radarKafkaProperties") KafkaProperties radarKafkaProperties){
        return new DefaultKafkaConsumerFactory(radarKafkaProperties.buildConsumerProperties());
    }


    @ConfigurationProperties(prefix = "sys.kafka.radar")
    @Bean
    public KafkaProperties radarKafkaProperties(){
        return new KafkaProperties();
    }
}
