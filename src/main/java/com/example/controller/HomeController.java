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

        List<Object[]> averageRatings = commentRepository.findAverageRatingsByDoctor();
        Map<UUID, Double> doctorAverageRatings = new HashMap<>();
        for (Object[] row : averageRatings) {
            UUID doctorId = (UUID) row[0];
            Double avgRating = (Double) row[1];
            doctorAverageRatings.put(doctorId, avgRating);
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("averageRatings", doctorAverageRatings);

        return "home";
    }
}
