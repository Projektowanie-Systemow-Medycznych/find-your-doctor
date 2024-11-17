package com.example.repository;

import com.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("SELECT c.doctor.id, AVG(c.rating) FROM Comment c GROUP BY c.doctor.id")
    List<Object[]> findAverageRatingsByDoctor();
}
