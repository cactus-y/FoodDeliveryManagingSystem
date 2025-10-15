// src/main/java/com/example/food_delivery_managing_system/file/controller/FileController.java
package com.example.food_delivery_managing_system.file.controller;

import com.example.food_delivery_managing_system.file.domain.StoredFile;
import com.example.food_delivery_managing_system.file.repository.StoredFileRepository;
import com.example.food_delivery_managing_system.file.service.S3StorageService;
import com.example.food_delivery_managing_system.user.eneity.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3StorageService s3;
    private final StoredFileRepository repository;

    /** 단일 파일 업로드 (이미지 전용)
     *  - dir: "profiles/{userId}" 같은 경로 힌트(선택)
     *  - 리턴: DB 정보 + 10분짜리 프리사인드 URL
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(@RequestPart("file") MultipartFile file,
                                     @RequestParam(value = "dir", required = false) String dir,
                                     @AuthenticationPrincipal CustomUserDetails principal) throws Exception {
        Long uploaderId = principal != null ? principal.getId() : null; // 인증 필수면 null 안 옴
        StoredFile saved = s3.uploadAndSave(file, uploaderId, dir);
        String url = s3.presignedGetUrl(saved.getS3Key(), Duration.ofMinutes(10)).toString();
        return FileUploadResponse.from(saved, url);
    }

    /** 파일 메타 조회(+ 새 프리사인드 URL 발급) */
    @GetMapping("/{id}")
    public FileUploadResponse get(@PathVariable Long id) {
        StoredFile sf = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("file not found: " + id));
        String url = s3.presignedGetUrl(sf.getS3Key(), Duration.ofMinutes(10)).toString();
        return FileUploadResponse.from(sf, url);
    }

    /** 키로 새 프리사인드 URL만 발급 (프론트에서 캐시 만료 시 재요청 용도) */
    @GetMapping("/signed")
    public SignedUrlResponse presigned(@RequestParam("key") String key,
                                       @RequestParam(value = "minutes", defaultValue = "10") long minutes) {
        String url = s3.presignedGetUrl(key, Duration.ofMinutes(minutes)).toString();
        return new SignedUrlResponse(key, url);
    }

    /** 삭제: S3 객체 삭제 + DB 레코드 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        StoredFile sf = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("file not found: " + id));
        s3.delete(sf.getS3Key());
        repository.deleteById(id);
    }

    // ---- DTOs ----
    @Data
    @Builder
    @AllArgsConstructor
    static class FileUploadResponse {
        private Long id;
        private String key;
        private String contentType;
        private long size;
        private String url; // 프리사인드 URL

        static FileUploadResponse from(StoredFile f, String url) {
            return FileUploadResponse.builder()
                    .id(f.getId())
                    .key(f.getS3Key())
                    .contentType(f.getContentType())
                    .size(f.getSize())
                    .url(url)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    static class SignedUrlResponse {
        private String key;
        private String url;
    }
}
