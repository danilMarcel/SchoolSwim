package com.swim.school.repository;

import com.swim.school.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUserIdAndIsActiveTrue(Long userId);
    List<UserSubscription> findByUserId(Long userId);
}
