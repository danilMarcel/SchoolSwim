package com.swim.school.service;

import com.swim.school.entity.*;
import com.swim.school.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleService scheduleService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public Booking createBooking(User user, Long scheduleId, LocalDate bookingDate, Long userSubscriptionId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        UserSubscription us = subscriptionService.getUserSubscriptionById(userSubscriptionId);

        if (!us.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Это не ваш абонемент");
        }
        if (us.getRemainingSessions() <= 0) {
            throw new RuntimeException("В абонементе нет оставшихся занятий");
        }

        if (bookingDate.equals(LocalDate.now()) && schedule.getStartTime().isBefore(LocalTime.now())) {
            throw new RuntimeException("Нельзя записаться на уже прошедшее занятие");
        }

        long confirmedCount = bookingRepository.countConfirmedByScheduleAndDate(scheduleId, bookingDate);
        if (confirmedCount >= schedule.getMaxCapacity()) {
            throw new RuntimeException("Нет свободных мест на это время");
        }

        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .userSubscription(us)
                .bookingDate(bookingDate)
                .status("CONFIRMED")
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId);
    }

    public List<Booking> getBookingsByScheduleAndDate(Long scheduleId, LocalDate date) {
        return bookingRepository.findByScheduleIdAndBookingDate(scheduleId, date);
    }

    public List<Booking> getBookingsByTrainerAndDate(Long trainerId, LocalDate date) {
        return bookingRepository.findByTrainerIdAndDate(trainerId, date);
    }

    @Transactional
    public void markAttendance(Long bookingId, boolean attended) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setAttended(attended);
        if (attended) {
            booking.setStatus("COMPLETED");
            subscriptionService.useSession(booking.getUserSubscription().getId());
        } else {
            booking.setStatus("CANCELLED");
        }
        bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByTrainerAndDateRange(Long trainerId, LocalDate start, LocalDate end) {
        return bookingRepository.findByTrainerIdAndDateRange(trainerId, start, end);
    }

    public double getRevenueForPeriod(LocalDate start, LocalDate end) {
        return bookingRepository.sumRevenueByDateRange(start, end);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
}
