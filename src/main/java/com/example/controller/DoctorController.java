package com.example.controller;

import com.example.model.AvailableSlot;
import com.example.model.Doctor;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.AvailableSlotRepository;
import com.example.repository.DoctorRepository;
import com.example.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private AvailableSlotRepository availableSlotRepository;

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

    @GetMapping("/reservation")
    public String showDoctorAvailableSlots(Model model, HttpSession session, @RequestParam("doctor_id") UUID doctor_id) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        Doctor doctor = doctorRepository.findById(doctor_id).orElse(null);

        List<AvailableSlot> availableSlots  = availableSlotRepository.findByDoctor(doctor);

        YearMonth currentYearMonth = YearMonth.now();
        int currentYear = currentYearMonth.getYear();
        int currentMonth = currentYearMonth.getMonthValue() - 1;

        List<String> availableSlotDates = availableSlots.stream()
                .filter(slot -> !slot.isReserved())
                .map(availableSlot -> availableSlot.getDatetime().toLocalDate().toString())
                .collect(Collectors.toList());

        model.addAttribute("availableSlots", availableSlotDates);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("doctorId", doctor.getId());
        return "doctor/doctor-available-slot";
    }

    @GetMapping("/reservation/day")
    public String showAvailableSlots(@RequestParam("date") String date, Model model, HttpSession session, @RequestParam("id") UUID doctor_id) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        Doctor doctor = doctorRepository.findById(doctor_id).orElse(null);

        List<AvailableSlot> availableSlots = availableSlotRepository.findByDoctor(doctor);

        List<AvailableSlot> availableSlotsDay = availableSlots.stream()
                .filter(slot -> !slot.isReserved())
                .filter(availableSlot -> availableSlot.getDatetime().toLocalDate().toString().equals(date))
                .collect(Collectors.toList());

        model.addAttribute("availableSlots", availableSlotsDay);
        model.addAttribute("date", date);
        return "doctor/doctor-available-slot-day";
    }

    @PostMapping("/reserve")
    public String reserveSlot(@RequestParam("slotId") UUID slotId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        AvailableSlot slot = availableSlotRepository.findById(slotId).orElse(null);
        if (slot == null || slot.isReserved()) {
            model.addAttribute("error", "Slot is not available");
            return "redirect:/";
        }

        slot.setReserved(true);
        availableSlotRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setUser(loggedInUser);
        reservation.setSlot(slot);
        reservationRepository.save(reservation);

        return "redirect:/";
    }



}
