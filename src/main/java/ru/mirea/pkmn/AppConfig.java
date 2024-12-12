package ru.mirea.pkmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mirea.pkmn.utils.ResourceFileLoader;


@Configuration
public class AppConfig {

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger("ApplicationLogger");
    }

    @Bean
    public ResourceFileLoader loader(Logger logger) {

        return new ResourceFileLoader(logger);
    }
}
