package _4.TourismContest.weather.repository;

import _4.TourismContest.weather.domain.WeatherForecast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    List<WeatherForecast> findByNxAndNyAndFcstTimeAndCategory(int nx, int ny, LocalDateTime fcstTime, String category);

    List<WeatherForecast> findAllByNxAndNyAndCategoryAndFcstTimeBetween(int nx, int ny, String category, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Page<WeatherForecast> findByNxAndNyAndCategoryAndFcstTimeIsAfter(int nx, int ny, String category ,LocalDateTime time, Pageable pageable);

    Page<WeatherForecast> findByNxAndNyAndCategoryAndFcstTimeBetween(int nx, int ny, String category ,LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Optional<WeatherForecast> findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(int nx,int ny, String category ,LocalDateTime gameTime);
}
