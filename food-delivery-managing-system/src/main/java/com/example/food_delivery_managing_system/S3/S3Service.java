package com.example.food_delivery_managing_system.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;       // APP_S3_BUCKET
    private final String keyPrefix;        // APP_S3_PREFIX (예: uploads/)
    private final String publicBaseUrl;    // APP_S3_PUBLIC_BASE_URL (예: https://bucket.s3.ap-northeast-2.amazonaws.com)

    public S3Service(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.prefix}") String keyPrefix,
            @Value("${aws.s3.public-base-url}") String publicBaseUrl,
            @Value("${aws.region}") String awsRegion // 있으면 사용
    ) {
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("APP_S3_BUCKET is required.");
        }
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            throw new IllegalStateException("APP_S3_PUBLIC_BASE_URL is required.");
        }

        this.bucketName = bucketName;
        this.keyPrefix  = normalizePrefix(keyPrefix);
        this.publicBaseUrl = stripTrailingSlash(publicBaseUrl);

        String regionStr = !awsRegion.isBlank() ? awsRegion : inferRegionFromBaseUrl(this.publicBaseUrl);
        Region region = Region.of(regionStr);

        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /** 파일 업로드: 업로드 후 공개 URL 반환 */
    public String uploadFile(MultipartFile file) throws IOException {
        String key = buildObjectKey(file.getOriginalFilename());

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        return publicBaseUrl + "/" + key;
    }

    /** 삭제 */
    public void deleteFile(String fileKeyOrUrl) {
        String key = extractKey(fileKeyOrUrl);
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(req);
    }

    /** 리스트 */
    public List<String> listFiles() {
        ListObjectsV2Response res = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(keyPrefix)
                .build());
        return res.contents().stream().map(S3Object::key).toList();
    }

    // ----------------- helpers -----------------

    private String buildObjectKey(String originalFilename) {
        String safeName = (originalFilename == null || originalFilename.isBlank()) ? "file" : originalFilename;
        String unique = UUID.randomUUID() + "-" + safeName;
        return keyPrefix + unique;
    }

    private static String normalizePrefix(String p) {
        if (p == null || p.isBlank()) return "";
        String v = p.trim();
        // "uploads", "uploads/" → "uploads/"
        return v.endsWith("/") ? v : (v + "/");
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** URL 또는 key를 받아 key만 뽑아내기 */
    private String extractKey(String fileKeyOrUrl) {
        if (fileKeyOrUrl == null) return "";
        if (fileKeyOrUrl.startsWith("http://") || fileKeyOrUrl.startsWith("https://")) {
            // publicBaseUrl 뒤의 path만 key로 사용
            String base = publicBaseUrl + "/";
            if (fileKeyOrUrl.startsWith(base)) {
                return fileKeyOrUrl.substring(base.length());
            }
            // 다른 도메인이라면 대략적으로 host 이후만 파싱
            int idx = fileKeyOrUrl.indexOf(".amazonaws.com/");
            return (idx > 0) ? fileKeyOrUrl.substring(idx + ".amazonaws.com/".length()) : fileKeyOrUrl;
        }
        return fileKeyOrUrl;
    }

    /** public base url에서 리전 추론 (s3.ap-northeast-2.amazonaws.com) */
    private static String inferRegionFromBaseUrl(String baseUrl) {
        Pattern p = Pattern.compile("s3\\.(.*?)\\.amazonaws\\.com");
        Matcher m = p.matcher(baseUrl);
        if (m.find()) return m.group(1);
        return "ap-northeast-2";
    }

    public void deleteFileByUrl(String oldUrl) {
        if (oldUrl == null || oldUrl.isBlank()) return;

        try {
            String key = extractKey(oldUrl);
            if (key == null || key.isBlank()) return;

            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(req);
            System.out.println("[S3] Deleted: " + key);
        } catch (S3Exception e) {
            System.err.println("[S3] Delete failed: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("[S3] Delete failed: " + e.getMessage());
        }
    }
}
