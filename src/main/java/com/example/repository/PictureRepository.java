package com.example.repository;

import com.example.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface PictureRepository extends JpaRepository<Picture, UUID> {
}
