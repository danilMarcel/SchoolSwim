package com.swim.school.config;

import com.swim.school.entity.*;
import com.swim.school.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingGroupRepository trainingGroupRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Администратор")
                    .email("admin@swim.ru")
                    .role(Role.ROLE_ADMIN)
                    .build());
        }

        refreshTrainers();
        refreshSchedules();

        if (subscriptionRepository.count() == 0) {
            subscriptionRepository.save(Subscription.builder()
                    .name("Индивидуальное занятие")
                    .description("1 занятие 45 минут с тренером")
                    .price(BigDecimal.valueOf(800))
                    .durationMinutes(45).maxStudents(1).sessionsCount(1)
                    .isActive(true)
                    .build());
            subscriptionRepository.save(Subscription.builder()
                    .name("Парное занятие")
                    .description("Занятие 45 минут для двух учеников")
                    .price(BigDecimal.valueOf(1200))
                    .durationMinutes(45).maxStudents(2).sessionsCount(1)
                    .isActive(true)
                    .build());
        }
    }

    private void refreshTrainers() {
        Trainer t1 = findOrCreateTrainer(1L, "Ракитин Дмитрий Юрьевич");
        t1.setDescription("Тренер по плаванию");
        t1.setExperience("12 лет");
        t1.setEducation("ВГАС, кафедра плавания");
        t1.setPhotoUrl("/images/trainer2.jpg");
        trainerRepository.save(t1);

        Trainer t2 = findOrCreateTrainer(2L, "Ветохин Данила Владимирович");
        t2.setDescription("Тренер по плаванию");
        t2.setExperience("2 года, кандитат в мастера спорта по плаванию");
        t2.setPhotoUrl("/images/trainer1.jpg");
        trainerRepository.save(t2);

        Trainer t3 = findOrCreateTrainer(3L, "Уварова Мария Сергеевна");
        t3.setDescription("Тренер по плаванию");
        t3.setExperience("8 лет");
        t3.setEducation("ВГАС, кафедра плавания");
        t3.setPhotoUrl("/images/trainer3.jpg");
        trainerRepository.save(t3);

        Trainer t4 = findOrCreateTrainer(4L, "Паневкин Сергей Григорьевич");
        t4.setDescription("Тренер по плаванию");
        t4.setExperience("40 лет, мастер спорта СССР по плаванию");
        t4.setEducation("ВГАС, кафедра плавания");
        t4.setPhotoUrl("/images/trainer5.jpg");
        trainerRepository.save(t4);
    }

    private Trainer findOrCreateTrainer(Long id, String name) {
        return trainerRepository.findById(id).orElseGet(() ->
                trainerRepository.save(Trainer.builder().name(name).build()));
    }

    private void refreshSchedules() {
        Trainer t1 = trainerRepository.findById(1L).orElse(null);
        Trainer t2 = trainerRepository.findById(2L).orElse(null);
        Trainer t3 = trainerRepository.findById(3L).orElse(null);
        Trainer t4 = trainerRepository.findById(4L).orElse(null);


        if (t1 == null || t2 == null) return;
        if (t3 == null || t4 == null) return;


        TrainingGroup smallPool = findOrCreateGroup("Малая чаша", "SMALL", 2, 6, t1);
        TrainingGroup bigPool = findOrCreateGroup("Большая чаша", "BIG", 7, 18, t2);

        bookingRepository.deleteAll();
        scheduleRepository.deleteAll();

        scheduleRepository.save(Schedule.builder()
                .trainingGroup(smallPool).trainer(t1)
                .dayOfWeek("Понедельник").startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(10, 45))
                .maxCapacity(8).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(smallPool).trainer(t1)
                .dayOfWeek("Вторник").startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(10, 45))
                .maxCapacity(8).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(smallPool).trainer(t1)
                .dayOfWeek("Среда").startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(10, 45))
                .maxCapacity(8).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(bigPool).trainer(t2)
                .dayOfWeek("Четверг").startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(11, 45))
                .maxCapacity(10).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(bigPool).trainer(t2)
                .dayOfWeek("Пятница").startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(11, 45))
                .maxCapacity(10).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(bigPool).trainer(t2)
                .dayOfWeek("Суббота").startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(11, 45))
                .maxCapacity(10).pricePerSession(BigDecimal.valueOf(800))
                .build());
        scheduleRepository.save(Schedule.builder()
                .trainingGroup(bigPool).trainer(t2)
                .dayOfWeek("Воскресенье").startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(11, 45))
                .maxCapacity(10).pricePerSession(BigDecimal.valueOf(800))
                .build());
    }

    private TrainingGroup findOrCreateGroup(String name, String poolType, int minAge, int maxAge, Trainer trainer) {
        return trainingGroupRepository.findByPoolType(poolType).stream().findFirst().orElseGet(() ->
                trainingGroupRepository.save(TrainingGroup.builder()
                        .name(name).poolType(poolType).minAge(minAge).maxAge(maxAge).trainer(trainer)
                        .build()));
    }
}
