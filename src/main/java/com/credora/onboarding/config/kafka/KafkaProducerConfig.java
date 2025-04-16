package com.credora.onboarding.config.kafka;

import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, OnboardingNotificationRequest> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put("spring.json.type.mapping", "onboardingConfirmation:com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest");
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, OnboardingNotificationRequest> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
