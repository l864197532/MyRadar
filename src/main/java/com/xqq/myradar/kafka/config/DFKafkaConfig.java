package com.xqq.myradar.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

public class DFKafkaConfig {

    @Value("${sys.kafka.df.producer.transaction-id-prefix}")
    private String transactionIdPrefix;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(@Autowired @Qualifier("dfProducerFactory") ProducerFactory dfProducerFactory) {
        return new KafkaTemplate<>(dfProducerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory dfKafkaFactory(@Autowired @Qualifier("dfConsumerFactory") ConsumerFactory dfConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(dfConsumerFactory);
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//手动提交

        return factory;
    }

    @Bean
    public ProducerFactory dfProducerFactory(@Autowired @Qualifier("dfKafkaProperties") KafkaProperties dfKafkaProperties){
        DefaultKafkaProducerFactory dkp =  new DefaultKafkaProducerFactory(dfKafkaProperties.buildProducerProperties());
       // dkp.setTransactionIdPrefix(transactionIdPrefix);
        return dkp;
    }

    @Bean
    public ConsumerFactory dfConsumerFactory(@Autowired @Qualifier("dfKafkaProperties") KafkaProperties dfKafkaProperties){
        return new DefaultKafkaConsumerFactory(dfKafkaProperties.buildConsumerProperties());
    }

    @Bean
    public AdminClient dfAdminClient(@Autowired @Qualifier("dfKafkaProperties") KafkaProperties dfKafkaProperties){
       return AdminClient.create(dfKafkaProperties.buildAdminProperties());
    }

    @ConfigurationProperties(prefix = "sys.kafka.df")
    @Bean
    public KafkaProperties dfKafkaProperties(){
        return new KafkaProperties();
    }

}
