package upt.gestaodespesas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestaoDespesasApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestaoDespesasApplication.class, args);
    }
}
