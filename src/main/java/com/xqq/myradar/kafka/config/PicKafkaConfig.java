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

public class PicKafkaConfig {
 
    @Bean
    public KafkaListenerContainerFactory picKafkaFactory(@Autowired @Qualifier("picConsumerFactory") ConsumerFactory picConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(picConsumerFactory);
        //factory.setConcurrency(10);
        //factory.setBatchListener(true);
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//手动提交
 
        return factory;
    }

    @Primary
    @Bean
    public ConsumerFactory picConsumerFactory(@Autowired @Qualifier("picKafkaProperties") KafkaProperties picKafkaProperties){
        return new DefaultKafkaConsumerFactory(picKafkaProperties.buildConsumerProperties());
    }
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "sys.kafka.pic")
    public KafkaProperties picKafkaProperties(){
        return new KafkaProperties();
    }
 
}