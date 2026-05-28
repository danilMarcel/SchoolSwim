package com.swim.school.controller;

import com.swim.school.entity.Trainer;
import com.swim.school.entity.TrainingGroup;
import com.swim.school.entity.Schedule;
import com.swim.school.service.TrainerService;
import com.swim.school.service.TrainingGroupService;
import com.swim.school.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final TrainerService trainerService;
    private final TrainingGroupService trainingGroupService;
    private final ScheduleService scheduleService;

    @GetMapping("/")
    public String index(Model model) {
        List<TrainingGroup> groups = trainingGroupService.getAllGroups();
        model.addAttribute("groups", groups);
        return "index";
    }

    @GetMapping("/trainers")
    public String trainers(Model model) {
        List<Trainer> trainers = trainerService.getAllTrainers();
        model.addAttribute("trainers", trainers);
        return "trainers";
    }

    @GetMapping("/prices")
    public String prices(Model model) {
        List<TrainingGroup> groups = trainingGroupService.getAllGroups();
        model.addAttribute("groups", groups);
        return "prices";
    }

    @GetMapping("/schedule")
    public String schedule(@RequestParam(required = false) Long trainer,
                           @RequestParam(required = false) String day,
                           Model model) {
        List<Schedule> schedules;
        if (trainer != null && day != null && !day.isEmpty()) {
            schedules = scheduleService.getSchedulesByTrainerAndDay(trainer, day);
        } else if (trainer != null) {
            schedules = scheduleService.getSchedulesByTrainer(trainer);
        } else if (day != null && !day.isEmpty()) {
            schedules = scheduleService.getSchedulesByDayOfWeek(day);
        } else {
            schedules = scheduleService.getAllSchedules();
        }
        List<Trainer> trainers = trainerService.getAllTrainers();
        model.addAttribute("schedules", schedules);
        model.addAttribute("trainers", trainers);
        model.addAttribute("days", new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"});
        return "schedule";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

}
