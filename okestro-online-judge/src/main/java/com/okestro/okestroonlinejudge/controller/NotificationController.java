package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 실시간 알림 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Notification", description = "실시간 알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 제출 상태 구독 (SSE).
     *
     * @param submissionId 제출 ID
     * @return SseEmitter
     */
    @Operation(summary = "제출 상태 구독", description = "Server-Sent Events를 통해 실시간 채점 상태를 구독합니다.")
    @GetMapping(value = "/subscribe/submission/{submissionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeSubmission(@PathVariable Long submissionId) {
        return notificationService.subscribe(submissionId);
    }
}

