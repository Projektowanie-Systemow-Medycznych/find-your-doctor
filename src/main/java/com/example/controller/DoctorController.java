package com.example.controller;

import com.example.model.Doctor;
import com.example.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping()
    public String showDoctorProfile(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }
        model.addAttribute("doctor", loggedInDoctor);
        return "doctor/doctor-profile";
    }

    @GetMapping("/login")
    public String showDoctorLoginPage() {
        return "doctor/doctor-login";
    }

    @PostMapping("/login")
    public String processDoctorLogin(@RequestParam("login") String login,
                                     @RequestParam("password") String password,
                                     Model model, HttpSession session) {
        Doctor doctor = doctorRepository.findByLogin(login).orElse(null);
        if (doctor != null && doctor.getPassword().equals(password)) {
            session.setAttribute("loggedInDoctor", doctor);
            return "redirect:/doctor";
        } else {
            model.addAttribute("error", "Invalid login or password");
            return "doctor/doctor-login";
        }
    }

    @GetMapping("/edit-profile")
    public String editDoctorProfileForm(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }
        model.addAttribute("doctor", loggedInDoctor);
        return "doctor/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateDoctorProfile(@ModelAttribute("doctor") Doctor doctor, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }
        loggedInDoctor.setName(doctor.getName());
        loggedInDoctor.setLogin(doctor.getLogin());
        loggedInDoctor.setPassword(doctor.getPassword());
        doctorRepository.save(loggedInDoctor);
        session.setAttribute("loggedInDoctor", loggedInDoctor);
        return "redirect:/doctor";
    }

    @GetMapping("/register")
    public String showDoctorRegistrationForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctor/doctor-register";
    }

    @PostMapping("/register")
    public String processDoctorRegistration(@ModelAttribute("doctor") Doctor doctor, Model model) {
        if (doctorRepository.findByLogin(doctor.getLogin()).isPresent()) {
            model.addAttribute("error", "Login already taken");
            return "doctor/doctor-register";
        }
        doctorRepository.save(doctor);
        return "redirect:/doctor/login";
    }

    @GetMapping("/logout")
    public String logoutDoctor(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
