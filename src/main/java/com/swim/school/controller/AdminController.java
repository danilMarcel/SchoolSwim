package com.swim.school.controller;

import com.swim.school.entity.*;
import com.swim.school.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TrainerService trainerService;
    private final TrainingGroupService trainingGroupService;
    private final ScheduleService scheduleService;
    private final SubscriptionService subscriptionService;
    private final BookingService bookingService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("trainersCount", trainerService.getAllTrainers().size());
        model.addAttribute("subscriptionsCount", subscriptionService.getAllSubscriptions().size());
        model.addAttribute("schedulesCount", scheduleService.getAllSchedules().size());
        return "admin/dashboard";
    }

    @GetMapping("/trainers")
    public String trainers(Model model) {
        model.addAttribute("trainers", trainerService.getAllTrainers());
        return "admin/trainers";
    }

    @PostMapping("/trainers/save")
    public String saveTrainer(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam String experience,
                              @RequestParam String education,
                              @RequestParam(required = false) String photoUrl) {
        Trainer trainer = Trainer.builder()
                .id(id)
                .name(name)
                .description(description)
                .experience(experience)
                .education(education)
                .photoUrl(photoUrl)
                .build();
        trainerService.saveTrainer(trainer);
        return "redirect:/admin/trainers";
    }

    @GetMapping("/trainers/delete/{id}")
    public String deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return "redirect:/admin/trainers";
    }

    @GetMapping("/subscriptions")
    public String subscriptions(Model model) {
        model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());
        return "admin/subscriptions";
    }

    @PostMapping("/subscriptions/save")
    public String saveSubscription(@RequestParam(required = false) Long id,
                                   @RequestParam String name,
                                   @RequestParam String description,
                                   @RequestParam java.math.BigDecimal price,
                                   @RequestParam Integer sessionsCount,
                                   @RequestParam(required = false) Integer durationMinutes,
                                   @RequestParam(required = false) Integer maxStudents) {
        Subscription sub = Subscription.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .sessionsCount(sessionsCount)
                .durationMinutes(durationMinutes)
                .maxStudents(maxStudents)
                .isActive(true)
                .build();
        subscriptionService.saveSubscription(sub);
        return "redirect:/admin/subscriptions";
    }

    @GetMapping("/schedules")
    public String schedules(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        model.addAttribute("trainers", trainerService.getAllTrainers());
        model.addAttribute("groups", trainingGroupService.getAllGroups());
        model.addAttribute("days", new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"});
        return "admin/schedules";
    }

    @PostMapping("/schedules/save")
    public String saveSchedule(@RequestParam(required = false) Long id,
                               @RequestParam Long trainerId,
                               @RequestParam Long groupId,
                               @RequestParam String dayOfWeek,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               @RequestParam Integer maxCapacity) {
        Schedule schedule = Schedule.builder()
                .id(id)
                .trainer(trainerService.getTrainerById(trainerId))
                .trainingGroup(trainingGroupService.getGroupById(groupId))
                .dayOfWeek(dayOfWeek)
                .startTime(java.time.LocalTime.parse(startTime))
                .endTime(java.time.LocalTime.parse(endTime))
                .maxCapacity(maxCapacity)
                .build();
        scheduleService.saveSchedule(schedule);
        return "redirect:/admin/schedules";
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        List<Trainer> trainers = trainerService.getAllTrainers();
        model.addAttribute("trainers", trainers);
        model.addAttribute("today", LocalDate.now());
        return "admin/attendance";
    }

    @PostMapping("/attendance/load")
    public String loadAttendance(@RequestParam Long trainerId,
                                 @RequestParam LocalDate date,
                                 Model model) {
        List<Booking> bookings = bookingService.getBookingsByTrainerAndDate(trainerId, date);
        model.addAttribute("bookings", bookings);
        model.addAttribute("trainers", trainerService.getAllTrainers());
        model.addAttribute("selectedTrainerId", trainerId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("today", LocalDate.now());
        return "admin/attendance";
    }

    @PostMapping("/attendance/mark")
    public String markAttendance(@RequestParam Long bookingId,
                                 @RequestParam boolean attended,
                                 @RequestParam Long trainerId,
                                 @RequestParam LocalDate date) {
        bookingService.markAttendance(bookingId, attended);
        return "redirect:/admin/attendance?trainerId=" + trainerId + "&date=" + date;
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        double revenue = bookingService.getRevenueForPeriod(startOfMonth, endOfMonth);
        model.addAttribute("revenue", revenue);
        model.addAttribute("startDate", startOfMonth);
        model.addAttribute("endDate", endOfMonth);
        model.addAttribute("trainers", trainerService.getAllTrainers());
        return "admin/reports";
    }

    @PostMapping("/reports/filter")
    public String reportsFilter(@RequestParam(required = false) Long trainerId,
                                @RequestParam LocalDate startDate,
                                @RequestParam LocalDate endDate,
                                Model model) {
        double revenue;
        if (trainerId != null && trainerId > 0) {
            List<Booking> bookings = bookingService.getBookingsByTrainerAndDateRange(trainerId, startDate, endDate);
            revenue = bookings.stream()
                    .filter(b -> b.getAttended() != null && b.getAttended())
                    .mapToDouble(b -> b.getUserSubscription().getSubscription().getPrice().doubleValue())
                    .sum();
        } else {
            revenue = bookingService.getRevenueForPeriod(startDate, endDate);
        }
        model.addAttribute("revenue", revenue);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("trainers", trainerService.getAllTrainers());
        model.addAttribute("selectedTrainerId", trainerId);
        return "admin/reports";
    }
}
