package com.credora.onboarding.config.kafka;

import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, OnboardingNotificationRequest> kafkaTemplate;

  public void sendOnboardingConfirmationNotification(OnboardingNotificationRequest request) {
    log.info("Sending onboarding confirmation event: {}", request);

    Message<OnboardingNotificationRequest> message = MessageBuilder
            .withPayload(request)
            .setHeader(TOPIC, "onboarding-confirmation")
            .build();

    CompletableFuture<SendResult<String, OnboardingNotificationRequest>> future = kafkaTemplate.send(message);

    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
      } else {
        log.error("Unable to send message due to: {}", ex.getMessage());
      }
    });
  }
}
