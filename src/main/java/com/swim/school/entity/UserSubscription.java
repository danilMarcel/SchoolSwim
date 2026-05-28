package com.swim.school.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "remaining_sessions", nullable = false)
    private Integer remainingSessions;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    protected void onInit() {
        purchaseDate = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (remainingSessions == null && subscription != null) {
            remainingSessions = subscription.getSessionsCount();
        }
    }
}
