package com.swim.school.service;

import com.swim.school.entity.TrainingGroup;
import com.swim.school.repository.TrainingGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingGroupService {

    private final TrainingGroupRepository trainingGroupRepository;

    public List<TrainingGroup> getAllGroups() {
        return trainingGroupRepository.findAll();
    }

    public List<TrainingGroup> getGroupsByPoolType(String poolType) {
        return trainingGroupRepository.findByPoolType(poolType);
    }

    public TrainingGroup getGroupById(Long id) {
        return trainingGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public TrainingGroup saveGroup(TrainingGroup group) {
        return trainingGroupRepository.save(group);
    }
}
