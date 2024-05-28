package com.william.notix.seeders.imp;

import com.william.notix.seeders.Seeder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
@RequiredArgsConstructor
public class FindingSeeder implements Seeder {

    @Override
    public void run() throws Exception {}
}
