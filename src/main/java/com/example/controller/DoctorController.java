package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AvailableSlotRepository availableSlotRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PictureRepository pictureRepository;


    @GetMapping()
    public String showDoctorProfile(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }
        List<Comment> comments = commentRepository.findByDoctorOrderByTimestampDesc(loggedInDoctor);
        model.addAttribute("comments", comments);
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
        if (session.getAttribute("loggedInDoctor") != null) {
            model.addAttribute("error", "You are logged in as a doctor. Please log out from the doctor account.");
            return "doctor/doctor-login";
        }
        if (session.getAttribute("loggedInUser") != null) {
            model.addAttribute("error", "You are already logged in as a user. Please log out first.");
            return "doctor/doctor-login";
        }
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
    public String updateDoctorProfile(@ModelAttribute("doctor") Doctor doctor, HttpSession session,
                                      @RequestParam("file") MultipartFile file) throws IOException {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        if (!file.isEmpty()) {
            Picture p =new Picture();
            p.setData(file.getBytes());
            Picture updatedPicture = pictureRepository.save(p);
            loggedInDoctor.setPicture(updatedPicture);
        }

        loggedInDoctor.setName(doctor.getName());
        loggedInDoctor.setLogin(doctor.getLogin());
        loggedInDoctor.setPassword(doctor.getPassword());
        loggedInDoctor.setDescription(doctor.getDescription());
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

    @GetMapping("/add-available-slot")
    public String showAddAvailableSlotForm(Model model, HttpSession session) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        return "doctor/add-available-slot";
    }

    @PostMapping("/add-available-slot")
    public String addAvailableSlot(@RequestParam("datetime") String datetime,
                                   @RequestParam("address") String address,
                                   @RequestParam("price") String price,
                                   HttpSession session, Model model) {
        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }

        AvailableSlot slot = new AvailableSlot();
        slot.setDatetime(LocalDateTime.parse(datetime));
        slot.setAddress(address);
        slot.setDoctor(loggedInDoctor);
        slot.setReserved(false);
        slot.setPrice(Float.parseFloat(price));

        availableSlotRepository.save(slot);

        model.addAttribute("doctor", loggedInDoctor);
        return "redirect:/doctor";
    }

}
