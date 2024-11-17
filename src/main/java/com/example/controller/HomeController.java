package com.example.controller;

import com.example.model.Doctor;
import com.example.repository.CommentRepository;
import com.example.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Doctor> doctors = doctorRepository.findAll();

        List<Object[]> averageRatingsAndCounts = commentRepository.findAverageRatingsAndCountsByDoctor();
        Map<UUID, Double> doctorAverageRatings = new HashMap<>();
        Map<UUID, Long> doctorCommentCounts = new HashMap<>();
        for (Object[] row : averageRatingsAndCounts) {
            UUID doctorId = (UUID) row[0];
            Double avgRating = (Double) row[1];
            Long count = (Long) row[2];
            doctorAverageRatings.put(doctorId, avgRating);
            doctorCommentCounts.put(doctorId, count);
        }

        for (Doctor doctor : doctors) {
            doctorCommentCounts.putIfAbsent(doctor.getId(), 0L);
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("averageRatings", doctorAverageRatings);
        model.addAttribute("commentCounts", doctorCommentCounts);

        return "home";
    }
}
