package com.swim.school.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookingDto {
    private Long scheduleId;
    private LocalDate bookingDate;
    private Long userSubscriptionId;
}
