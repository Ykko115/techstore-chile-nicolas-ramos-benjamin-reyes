package cl.techstore.api.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Publica de forma asíncrona los eventos de auditoría de inventario
 * (CREAR / MODIFICAR / ELIMINAR) en la cola Amazon SQS "techstore-audit-queue".
 */
@Service
public class AuditoriaPublisherService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaPublisherService.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.audit-queue-url}")
    private String queueUrl;

    public AuditoriaPublisherService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Async
    public void publicarEvento(String accion, Long productoId, String nombre, String usuario) {
        if (queueUrl == null || queueUrl.isBlank()) {
            log.warn("[Auditoria] aws.sqs.audit-queue-url no configurado; se omite el envío a SQS. "
                    + "accion={}, productoId={}", accion, productoId);
            return;
        }

        try {
            Map<String, Object> evento = new LinkedHashMap<>();
            evento.put("accion", accion);
            evento.put("productoId", productoId);
            evento.put("nombre", nombre);
            evento.put("usuario", usuario);
            evento.put("fecha", Instant.now().toString());

            String payload = objectMapper.writeValueAsString(evento);

            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(payload)
                    .build());

            log.info("[Auditoria] Evento publicado en SQS: {}", payload);
        } catch (Exception ex) {
            log.error("[Auditoria] Error al publicar evento de auditoría en SQS (accion={}, productoId={})",
                    accion, productoId, ex);
        }
    }
}
