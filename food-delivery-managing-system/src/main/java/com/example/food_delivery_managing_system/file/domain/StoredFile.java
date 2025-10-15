// src/main/java/com/example/food_delivery_managing_system/file/domain/StoredFile.java
package com.example.food_delivery_managing_system.file.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "stored_file")
@Getter @Setter @ToString
public class StoredFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** S3 object key (ex: profiles/1/uuid_filename.jpg) */
    @Column(nullable = false, length = 512)
    private String s3Key;

    @Column(nullable = false, length = 128)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    /** 업로더 유저 ID(옵션) */
    private Long uploaderUserId;

    @CreationTimestamp
    private OffsetDateTime createdAt;
}
