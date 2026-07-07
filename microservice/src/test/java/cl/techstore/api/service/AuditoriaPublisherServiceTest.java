package cl.techstore.api.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ExtendWith(MockitoExtension.class)
class AuditoriaPublisherServiceTest {

    @Mock
    private SqsClient sqsClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuditoriaPublisherService auditoriaPublisherService;

    @BeforeEach
    void setUp() {
        auditoriaPublisherService = new AuditoriaPublisherService(sqsClient, objectMapper);
    }

    @Test
    void publicarEvento_deberiaEnviarASiHayUrl() {
        ReflectionTestUtils.setField(auditoriaPublisherService, "queueUrl", "https://sqs.us-east-1.amazonaws.com/123/test-queue");

        auditoriaPublisherService.publicarEvento("CREAR", 1L, "TV", "admin@techstore.cl");

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publicarEvento_deberiaOmitirSiNoHayUrl() {
        ReflectionTestUtils.setField(auditoriaPublisherService, "queueUrl", "");

        auditoriaPublisherService.publicarEvento("CREAR", 1L, "TV", "admin@techstore.cl");

        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publicarEvento_deberiaOmitirSiUrlEsNull() {
        ReflectionTestUtils.setField(auditoriaPublisherService, "queueUrl", null);

        auditoriaPublisherService.publicarEvento("CREAR", 1L, "TV", "admin@techstore.cl");

        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publicarEvento_deberiaManejarErrorDeSQS() {
        ReflectionTestUtils.setField(auditoriaPublisherService, "queueUrl", "https://sqs.us-east-1.amazonaws.com/123/test-queue");
        doThrow(new RuntimeException("SQS error")).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        auditoriaPublisherService.publicarEvento("CREAR", 1L, "TV", "admin@techstore.cl");

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
    }
}
