package _4.TourismContest.user.application;

import org.springframework.stereotype.Service;

import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            User updated = User.builder()
                    .email(updatedUser.getEmail())
                    .password(updatedUser.getPassword())
                    .nickname(updatedUser.getNickname())
                    .profileImg(updatedUser.getProfileImg())
                    .build();
            return userRepository.save(updated);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}