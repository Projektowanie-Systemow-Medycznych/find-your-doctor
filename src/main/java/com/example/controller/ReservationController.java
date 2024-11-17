package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class ReservationController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private AvailableSlotRepository availableSlotRepository;
    @Autowired
    private CommentRepository commentRepository;

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

        return "user/doctor-available-slot";
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
        return "user/doctor-available-slot-day";
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
            return "user/doctor-available-slot-day";
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
