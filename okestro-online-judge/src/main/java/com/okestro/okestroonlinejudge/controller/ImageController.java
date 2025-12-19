package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 이미지 관리 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Image", description = "이미지 업로드/다운로드 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 업로드
     */
    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하고 URL을 반환합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String fileName = imageService.uploadImage(file);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("url", "/api/images/" + fileName);

            return ResponseEntity.ok(ApiResponse.success(result, 201));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.internalError("이미지 업로드에 실패했습니다."));
        }
    }

    /**
     * 이미지 다운로드
     */
    @Operation(summary = "이미지 다운로드", description = "저장된 이미지를 다운로드합니다.")
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        try {
            InputStream imageStream = imageService.downloadImage(fileName);
            String contentType = imageService.getContentType(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .body(new InputStreamResource(imageStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
