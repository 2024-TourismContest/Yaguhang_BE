package _4.TourismContest.weather.repository;

import _4.TourismContest.weather.domain.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    List<WeatherForecast> findByNxAndNyAndFcstDateAndFcstTimeAndCategory(int nx, int ny, String fcstDate, String fcstTime, String category);
//    List<WeatherForecast> findByNxAndNyFcstDateAndFcstTimeAndCategory(int nx, int ny, String fcstDate, String fcstTime, String category);
}
