package com.swim.school.service;

import com.swim.school.entity.Subscription;
import com.swim.school.entity.User;
import com.swim.school.entity.UserSubscription;
import com.swim.school.repository.SubscriptionRepository;
import com.swim.school.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByIsActiveTrue();
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public UserSubscription purchaseSubscription(User user, Long subscriptionId) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        UserSubscription us = UserSubscription.builder()
                .user(user)
                .subscription(subscription)
                .remainingSessions(subscription.getSessionsCount())
                .isActive(true)
                .build();
        return userSubscriptionRepository.save(us);
    }

    public List<UserSubscription> getUserSubscriptions(Long userId) {
        return userSubscriptionRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public List<UserSubscription> getAllUserSubscriptions(Long userId) {
        return userSubscriptionRepository.findByUserId(userId);
    }

    public UserSubscription getUserSubscriptionById(Long id) {
        return userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserSubscription not found"));
    }

    @Transactional
    public void useSession(Long userSubscriptionId) {
        UserSubscription us = getUserSubscriptionById(userSubscriptionId);
        if (us.getRemainingSessions() <= 0) {
            throw new RuntimeException("Нет оставшихся занятий");
        }
        us.setRemainingSessions(us.getRemainingSessions() - 1);
        if (us.getRemainingSessions() <= 0) {
            us.setIsActive(false);
        }
        userSubscriptionRepository.save(us);
    }
}
