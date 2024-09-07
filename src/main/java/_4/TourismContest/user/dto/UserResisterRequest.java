package _4.TourismContest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserResisterRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 6, max = 20)
        String password,
        @NotBlank
        @Size(min = 2, max = 30)
        String nickname) {
}