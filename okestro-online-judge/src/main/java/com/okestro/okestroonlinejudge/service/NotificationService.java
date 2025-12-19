package com.okestro.okestroonlinejudge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE(Server-Sent Events) 알림 서비스.
 * 실시간 채점 상태 전송을 담당합니다.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    // submissionId -> SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 특정 제출(submissionId)에 대한 알림을 구독합니다.
     *
     * @param submissionId 제출 ID
     * @return SseEmitter
     */
    public SseEmitter subscribe(Long submissionId) {
        // 타임아웃 5분 (채점이 길어질 수 있음)
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitters.put(submissionId, emitter);

        log.info("Subscribed to submission: {}", submissionId);

        // 연결 완료/타임아웃/에러 시 제거
        emitter.onCompletion(() -> {
            log.info("SSE Completed for submission: {}", submissionId);
            emitters.remove(submissionId);
        });
        emitter.onTimeout(() -> {
            log.info("SSE Timeout for submission: {}", submissionId);
            emitters.remove(submissionId);
        });
        emitter.onError((e) -> {
            log.error("SSE Error for submission: {}", submissionId, e);
            emitters.remove(submissionId);
        });

        // 초기 연결 확인 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to submission " + submissionId));
        } catch (IOException e) {
            log.error("Failed to send connect event", e);
            emitters.remove(submissionId);
        }

        return emitter;
    }

    /**
     * 특정 제출의 채점 상태를 전송합니다.
     *
     * @param submissionId 제출 ID
     * @param status 상태 메시지 (예: "Judging", "Accepted", "Wrong Answer")
     * @param data 추가 데이터 (선택 사항)
     */
    public void sendStatus(Long submissionId, String status, Object data) {
        SseEmitter emitter = emitters.get(submissionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("status")
                        .data(Map.of("status", status, "data", data != null ? data : "{}")));
                
                // 최종 상태인 경우 연결 종료
                if (isFinalStatus(status)) {
                    emitter.complete();
                    emitters.remove(submissionId);
                }
            } catch (IOException e) {
                log.error("Failed to send status event for submission: {}", submissionId, e);
                emitters.remove(submissionId);
            }
        }
    }

    private boolean isFinalStatus(String status) {
        return "Accepted".equalsIgnoreCase(status) ||
               "Wrong Answer".equalsIgnoreCase(status) ||
               "Time Limit Exceeded".equalsIgnoreCase(status) ||
               "Memory Limit Exceeded".equalsIgnoreCase(status) ||
               "Runtime Error".equalsIgnoreCase(status) ||
               "Compilation Error".equalsIgnoreCase(status);
    }
}

