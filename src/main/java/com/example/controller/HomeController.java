package com.example.controller;

import com.example.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "home";
    }
}
