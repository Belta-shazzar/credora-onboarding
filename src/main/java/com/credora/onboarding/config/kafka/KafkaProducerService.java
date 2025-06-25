package com.credora.onboarding.config.kafka;

import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private final KafkaTemplate<String, OnboardingNotificationRequest> kafkaTemplate;
  private final Tracer tracer;
  private final Propagator propagator;

  public void sendOnboardingConfirmationNotification(OnboardingNotificationRequest request) {
    Span span = tracer.nextSpan().name("onboarding-confirmation-producer").start();

    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      log.info("Sending onboarding confirmation event: {}", request);

      ProducerRecord<String, OnboardingNotificationRequest> record =
              new ProducerRecord<>("onboarding-confirmation", request);

      propagator.inject(span.context(), record.headers(), (headers, key, value) ->
              {
                assert headers != null;
                headers.add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
              }
      );

      // Send message directly to the topic instead of using headers
      CompletableFuture<SendResult<String, OnboardingNotificationRequest>> future =
              kafkaTemplate.send(record);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
                  result.getRecordMetadata().topic(),
                  result.getRecordMetadata().partition(),
                  result.getRecordMetadata().offset());

          span.tag("kafka.topic", result.getRecordMetadata().topic());
          span.tag("kafka.offset", String.valueOf(result.getRecordMetadata().offset()));
        } else {
          log.error("Unable to send message due to: {}", ex.getMessage());
          span.error(ex);
        }
        span.end();
      });
    } catch (Exception e) {
      span.error(e);
      span.end();
      throw e;
    }
  }
}
