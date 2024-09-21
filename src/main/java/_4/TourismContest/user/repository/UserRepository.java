package _4.TourismContest.user.repository;

import _4.TourismContest.oauth.domain.AuthProvider;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);
    Boolean existsByEmailAndProvider(String email, AuthProvider provider);
}
