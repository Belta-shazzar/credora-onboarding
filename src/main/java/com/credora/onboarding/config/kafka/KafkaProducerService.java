package com.credora.onboarding.config.kafka;

import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private final KafkaTemplate<String, OnboardingNotificationRequest> kafkaTemplate;

//  public void sendOnboardingConfirmationNotification(OnboardingNotificationRequest request) {
//    System.out.println(request);
//    log.info("Sending onboarding confirmation event: {}", request);
//
//    Message<OnboardingNotificationRequest> message = MessageBuilder
//            .withPayload(request)
//            .setHeader(TOPIC, "onboarding-confirmation")
//            .build();
//
//    System.out.println(message);
//    CompletableFuture<SendResult<String, OnboardingNotificationRequest>> future = kafkaTemplate.send(message);
//
//    future.whenComplete((result, ex) -> {
//      if (ex == null) {
//        log.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
//                result.getRecordMetadata().topic(),
//                result.getRecordMetadata().partition(),
//                result.getRecordMetadata().offset());
//      } else {
//        log.error("Unable to send message due to: {}", ex.getMessage());
//      }
//
//    });
//  }

  public void sendOnboardingConfirmationNotification(OnboardingNotificationRequest request) {
    log.info("Sending onboarding confirmation event: {}", request);

    try {
      // Send message directly to the topic instead of using headers
      CompletableFuture<SendResult<String, OnboardingNotificationRequest>> future =
              kafkaTemplate.send("onboarding-confirmation", request);

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
    } catch (Exception e) {
      log.error("Error sending message to Kafka: {}", e.getMessage(), e);
      throw e; // Re-throw to let the caller handle it
    }
  }
}
