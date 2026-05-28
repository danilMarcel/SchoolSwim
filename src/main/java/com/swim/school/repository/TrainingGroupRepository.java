package com.swim.school.repository;

import com.swim.school.entity.TrainingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainingGroupRepository extends JpaRepository<TrainingGroup, Long> {
    List<TrainingGroup> findByPoolType(String poolType);
    List<TrainingGroup> findByTrainerId(Long trainerId);
}
