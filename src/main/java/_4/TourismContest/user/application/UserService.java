package _4.TourismContest.user.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.exception.ResourceNotFoundException;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserProfileResponse;
import _4.TourismContest.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public String createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }
        userRepository.save(user);
        return "success create user";
    }

    public UserProfileResponse getCurrentUser(Long uid){
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", uid));
        return UserProfileResponse.of(user);
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