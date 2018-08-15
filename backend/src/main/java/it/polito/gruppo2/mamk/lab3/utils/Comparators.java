package it.polito.gruppo2.mamk.lab3.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Maybe an overkill in this case but supposing that our app has many comparators
@Configuration
public class Comparators {

    @Bean
    // Compares positions ordering them by ascending order of their timestamp
    public PositionsTimestampComparator positionsTimestampComparator(){ return new PositionsTimestampComparator(); }
}
