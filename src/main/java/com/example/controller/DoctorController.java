package com.example.controller;

import com.example.model.Doctor;
import com.example.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/login")
    public String showDoctorLoginPage() {
        return "doctor-login";
    }

    @PostMapping("/login")
    public String processDoctorLogin(@RequestParam("login") String login,
                                     @RequestParam("password") String password,
                                     Model model) {
        Doctor doctor = doctorRepository.findByLogin(login).orElse(null);
        if (doctor != null && doctor.getPassword().equals(password)) {
            return "doctor-home";
        } else {
            model.addAttribute("error", "Invalid login or password");
            return "doctor-login";
        }
    }
}
