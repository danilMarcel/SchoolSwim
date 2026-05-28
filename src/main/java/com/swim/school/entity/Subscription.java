package com.swim.school.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "sessions_count", nullable = false)
    private Integer sessionsCount;

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    protected void onInit() {
        if (isActive == null) isActive = true;
    }
}
