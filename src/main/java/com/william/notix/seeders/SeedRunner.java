package com.william.notix.seeders;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {

    private final List<Seeder> seeders;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Start seeding");
            final List<Seeder> orderedSeeder = orderSeeders();
            for (Seeder seeder : orderedSeeder) {
                Instant start = Instant.now();
                String seederName = seeder
                    .getClass()
                    .getSimpleName()
                    .split("\\$\\$")[0];
                seeder.run();
                Instant finish = Instant.now();
                long elapsedTime = Duration.between(start, finish).toMillis();
                log
                    .atInfo()
                    .setMessage("Execute [{}]: {}ms")
                    .addArgument(seederName)
                    .addArgument(elapsedTime)
                    .log();
            }
            log.info("Done seeding");
        } catch (Exception e) {
            log.error("Error when seeding", e);
        }
    }

    private List<Seeder> orderSeeders() {
        List<Seeder> orderedSeeders = new ArrayList<>(seeders);
        orderedSeeders.sort(
            Comparator.comparingInt(o ->
                o.getClass().getAnnotation(Order.class).value()
            )
        );
        return orderedSeeders;
    }
}
