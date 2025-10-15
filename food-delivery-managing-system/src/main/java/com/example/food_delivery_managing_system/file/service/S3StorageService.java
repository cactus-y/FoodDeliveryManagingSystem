// src/main/java/com/example/food_delivery_managing_system/file/service/S3StorageService.java
package com.example.food_delivery_managing_system.file.service;

import com.example.food_delivery_managing_system.file.domain.StoredFile;
import com.example.food_delivery_managing_system.file.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;
    private final StoredFileRepository repository;

    @Value("${app.s3.bucket}") private String bucket;
    /** 버킷이 public-read라면 true, private라면 false */
    @Value("${app.s3.public-bucket:true}") private boolean publicBucket;
    /** 리전 – public URL 만들 때 사용 */
    @Value("${cloud.aws.region.static:ap-northeast-2}") private String region;

    /**
     * 업로드 + 메타 저장. DB에는 StoredFile로 보관.
     * 반환: 저장된 StoredFile (s3Key 포함)
     */
    public StoredFile uploadAndSave(MultipartFile file, Long uploaderId, String dir) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("빈 파일");
        String ct = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
        if (!ct.startsWith("image/")) throw new IllegalArgumentException("이미지 파일만 업로드 가능");

        String safeName = Objects.requireNonNullElse(file.getOriginalFilename(), "file");
        String key = (dir == null || dir.isBlank() ? "" : dir.replaceAll("^/+", "").replaceAll("/+$", "") + "/")
                + UUID.randomUUID() + "_" + safeName;

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket).key(key).contentType(ct)
                .build();

        s3.putObject(put, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        StoredFile sf = new StoredFile();
        sf.setS3Key(key);
        sf.setContentType(ct);
        sf.setSize(file.getSize());
        sf.setUploaderUserId(uploaderId);
        return repository.save(sf);
    }

    public String signedGetUrl(String key, long minutes) {
        return presignedGetUrl(key, Duration.ofMinutes(minutes)).toString();
    }
    /** 공개 버킷일 때 사용할 수 있는 절대 URL */
    public String publicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    /** private 버킷이면 프리사인 URL 발급 */
    public URL presignedGetUrl(String key, Duration ttl) {
        try (S3Presigner presigner =
                     S3Presigner.builder().region(Region.of(region)).build()) {
            var getReq = GetObjectRequest.builder().bucket(bucket).key(key).build();
            var presign = GetObjectPresignRequest.builder()
                    .getObjectRequest(getReq)
                    .signatureDuration(ttl)
                    .build();
            return presigner.presignGetObject(presign).url();
        }
    }

    /** 버킷 유형(public/private)에 맞춰 최종적으로 노출할 URL 생성 */
    public String resolveServeUrl(String key) {
        if (key == null || key.isBlank()) return null;
        if (publicBucket) return publicUrl(key);
        return presignedGetUrl(key, Duration.ofMinutes(10)).toString();
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
