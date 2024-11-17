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
    private ReservationRepository reservationRepository;
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

    @GetMapping("/reservation")
    public String showDoctorAvailableSlots(Model model, HttpSession session, @RequestParam("doctor_id") UUID doctor_id) {
        Doctor doctor = doctorRepository.findById(doctor_id).orElse(null);

        List<AvailableSlot> availableSlots = availableSlotRepository.findByDoctor(doctor);

        YearMonth currentYearMonth = YearMonth.now();
        int currentYear = currentYearMonth.getYear();
        int currentMonth = currentYearMonth.getMonthValue() - 1;

        List<String> availableSlotDates = availableSlots.stream()
                .filter(slot -> !slot.isReserved())
                .map(availableSlot -> availableSlot.getDatetime().toLocalDate().toString())
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findByDoctorOrderByTimestampDesc(doctor);
        model.addAttribute("comments", comments);
        model.addAttribute("doctor", doctor);
        model.addAttribute("availableSlots", availableSlotDates);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("doctorId", doctor.getId());
        model.addAttribute("doctorName", doctor.getName());

        return "doctor/doctor-available-slot";
    }
    @GetMapping("/reservation/day")
    public String showAvailableSlots(@RequestParam("date") String date, Model model, HttpSession session, @RequestParam("id") UUID doctor_id) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        boolean isLogged = loggedInUser != null;
        Doctor doctor = doctorRepository.findById(doctor_id).orElse(null);

        List<AvailableSlot> availableSlots = availableSlotRepository.findByDoctor(doctor);

        List<AvailableSlot> availableSlotsDay = availableSlots.stream()
                .filter(slot -> !slot.isReserved())
                .filter(availableSlot -> availableSlot.getDatetime().toLocalDate().toString().equals(date))
                .collect(Collectors.toList());

        model.addAttribute("availableSlots", availableSlotsDay);
        model.addAttribute("date", date);
        model.addAttribute("isLogged", isLogged);
        return "doctor/doctor-available-slot-day";
    }

    @PostMapping("/reserve")
    public String reserveSlot(@RequestParam("slotId") UUID slotId, @RequestParam("date") String date, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            Optional<AvailableSlot> slotTemp = availableSlotRepository.findById(slotId);
            List<AvailableSlot> availableSlots = availableSlotRepository.findByDoctor(slotTemp.get().getDoctor());
            List<AvailableSlot> availableSlotsDay = availableSlots.stream()
                    .filter(slot -> !slot.isReserved())
                    .filter(availableSlot -> availableSlot.getDatetime().toLocalDate().toString().equals(date))
                    .collect(Collectors.toList());
            model.addAttribute("availableSlots", availableSlotsDay);
            model.addAttribute("isLogged", false);
            model.addAttribute("date", date);
            model.addAttribute("error", "To reserve visit you have to be logged in!");
            return "doctor/doctor-available-slot-day";
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

        availableSlotRepository.save(slot);

        model.addAttribute("doctor", loggedInDoctor);
        return "doctor/doctor-profile";
    }

}
