package _4.TourismContest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TourismContestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourismContestApplication.class, args);
	}
}