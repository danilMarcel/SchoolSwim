package com.swim.school.repository;

import com.swim.school.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTrainerId(Long trainerId);
    List<Schedule> findByDayOfWeek(String dayOfWeek);
    List<Schedule> findByTrainingGroupId(Long groupId);
    List<Schedule> findByTrainerIdAndDayOfWeek(Long trainerId, String dayOfWeek);
    long countByTrainerIdAndDayOfWeekAndStartTime(Long trainerId, String dayOfWeek, java.time.LocalTime startTime);
}
