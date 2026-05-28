package com.swim.school.controller;

import com.swim.school.entity.*;
import com.swim.school.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final BookingService bookingService;
    private final ScheduleService scheduleService;
    private final TrainerService trainerService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName());
        List<UserSubscription> subscriptions = subscriptionService.getAllUserSubscriptions(user.getId());
        List<Booking> bookings = bookingService.getUserBookings(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("bookings", bookings);
        model.addAttribute("today", LocalDate.now());
        return "profile";
    }

    @GetMapping("/profile/buy/{subscriptionId}")
    public String buySubscription(Authentication auth, @PathVariable Long subscriptionId) {
        User user = userService.findByUsername(auth.getName());
        subscriptionService.purchaseSubscription(user, subscriptionId);
        return "redirect:/profile?purchased";
    }

    @GetMapping("/profile/book")
    public String bookForm(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName());
        List<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(user.getId());
        List<Schedule> schedules = scheduleService.getAllSchedules();
        List<Trainer> trainers = trainerService.getAllTrainers();

        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("schedules", schedules);
        model.addAttribute("trainers", trainers);
        model.addAttribute("days", new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"});
        model.addAttribute("today", LocalDate.now());
        return "book";
    }

    @PostMapping("/profile/book")
    public String book(Authentication auth,
                       @RequestParam Long scheduleId,
                       @RequestParam LocalDate bookingDate,
                       @RequestParam Long userSubscriptionId,
                       RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName());
        try {
            bookingService.createBooking(user, scheduleId, bookingDate, userSubscriptionId);
            return "redirect:/profile?booked";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/book";
        }
    }

    @PostMapping("/profile/cancel/{bookingId}")
    public String cancelBooking(Authentication auth, @PathVariable Long bookingId) {
        User user = userService.findByUsername(auth.getName());
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking.getUser().getId().equals(user.getId())) {
            bookingService.markAttendance(bookingId, false);
        }
        return "redirect:/profile";
    }
}
