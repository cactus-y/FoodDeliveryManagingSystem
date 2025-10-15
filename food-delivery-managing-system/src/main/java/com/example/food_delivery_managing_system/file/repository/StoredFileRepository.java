// src/main/java/com/example/food_delivery_managing_system/file/repository/StoredFileRepository.java
package com.example.food_delivery_managing_system.file.repository;

import com.example.food_delivery_managing_system.file.domain.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
}
