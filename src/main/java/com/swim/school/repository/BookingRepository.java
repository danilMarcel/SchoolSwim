package com.swim.school.repository;

import com.swim.school.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);
    List<Booking> findByScheduleIdAndBookingDate(Long scheduleId, LocalDate bookingDate);
    long countByScheduleIdAndBookingDateAndStatus(Long scheduleId, LocalDate bookingDate, String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.id = :scheduleId AND b.bookingDate = :date AND b.status = 'CONFIRMED'")
    long countConfirmedByScheduleAndDate(@Param("scheduleId") Long scheduleId, @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.schedule.trainer.id = :trainerId AND b.bookingDate = :date ORDER BY b.schedule.startTime")
    List<Booking> findByTrainerIdAndDate(@Param("trainerId") Long trainerId, @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.schedule.trainer.id = :trainerId AND b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findByTrainerIdAndDateRange(@Param("trainerId") Long trainerId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.price), 0) FROM Booking b JOIN b.userSubscription us JOIN us.subscription s WHERE b.attended = true AND b.bookingDate BETWEEN :startDate AND :endDate")
    double sumRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
