package com.example.controller;

import com.example.model.Doctor;
import com.example.repository.CommentRepository;
import com.example.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/")
    public String showHomePage(Model model, @RequestParam(value = "searchTerm", required = false) String searchTerm) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctors(doctors, searchTerm);

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
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("averageRatings", doctorAverageRatings);
        model.addAttribute("commentCounts", doctorCommentCounts);

        return "home";
    }

    private List<Doctor> filterDoctors(List<Doctor> doctors, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return doctors;
        }

        String lowerCaseSearchTerm = searchTerm.toLowerCase();

        return doctors.stream()
                .filter(doctor -> (doctor.getName() != null && doctor.getName().toLowerCase().contains(lowerCaseSearchTerm)) ||
                        (doctor.getDescription() != null && doctor.getDescription().toLowerCase().contains(lowerCaseSearchTerm)))
                .collect(Collectors.toList());
    }
}
