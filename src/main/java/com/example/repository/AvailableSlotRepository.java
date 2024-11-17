package com.example.repository;

import com.example.model.AvailableSlot;
import com.example.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, UUID> {
    List<AvailableSlot> findByDoctor(Doctor doctor);
}
