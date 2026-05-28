package com.swim.school.service;

import com.swim.school.entity.Schedule;
import com.swim.school.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private static final Map<String, Integer> DAY_ORDER = Map.of(
        "Понедельник", 1, "Вторник", 2, "Среда", 3,
        "Четверг", 4, "Пятница", 5, "Суббота", 6, "Воскресенье", 7
    );

    private final Comparator<Schedule> SCHEDULE_COMPARATOR = Comparator
        .comparingInt((Schedule s) -> DAY_ORDER.getOrDefault(s.getDayOfWeek(), 99))
        .thenComparing(Schedule::getStartTime);

    public List<Schedule> getAllSchedules() {
        List<Schedule> list = scheduleRepository.findAll();
        list.sort(SCHEDULE_COMPARATOR);
        return list;
    }

    public List<Schedule> getSchedulesByTrainer(Long trainerId) {
        List<Schedule> list = scheduleRepository.findByTrainerId(trainerId);
        list.sort(SCHEDULE_COMPARATOR);
        return list;
    }

    public List<Schedule> getSchedulesByDayOfWeek(String dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    public List<Schedule> getSchedulesByGroup(Long groupId) {
        List<Schedule> list = scheduleRepository.findByTrainingGroupId(groupId);
        list.sort(SCHEDULE_COMPARATOR);
        return list;
    }

    public List<Schedule> getSchedulesByTrainerAndDay(Long trainerId, String dayOfWeek) {
        List<Schedule> list = scheduleRepository.findByTrainerIdAndDayOfWeek(trainerId, dayOfWeek);
        list.sort(SCHEDULE_COMPARATOR);
        return list;
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public boolean isScheduleConflict(Long trainerId, String dayOfWeek, LocalTime startTime) {
        return scheduleRepository.countByTrainerIdAndDayOfWeekAndStartTime(trainerId, dayOfWeek, startTime) > 0;
    }
}
