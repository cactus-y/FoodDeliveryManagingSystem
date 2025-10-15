package com.example.food_delivery_managing_system.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final S3Service s3Service;
    /**
     * 파일 업로드 /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

//    /**
//     * bucket에 있는 전체 파일 목록 조회
//     */
//    @GetMapping("/list")
//    public ResponseEntity<List<String>> listFiles() {
//        List<String> files = s3Service.listFiles();
//        return ResponseEntity.ok(files);
//    }

    /**
     * 파일 삭제
     */
//    @DeleteMapping("/{fileName}")
//    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
//        try {
//            s3Service.deleteFile(fileName);
//            return ResponseEntity.ok("파일 삭제 성공");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("파일 삭제 실패: " + e.getMessage());
//        }
//    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteFileByUrl(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }

        try {
            // S3 키 추출
            String key = extractKeyFromUrl(imageUrl);
            s3Service.deleteFile(key);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("S3 삭제 실패: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        int idx = imageUrl.indexOf(".amazonaws.com/");
        return idx == -1 ? imageUrl : imageUrl.substring(idx + ".amazonaws.com/".length());
    }
}
