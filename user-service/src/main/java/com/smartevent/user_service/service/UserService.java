package com.smartevent.user_service.service;

import com.smartevent.user_service.dto.request.UpdateUserRequest;
import com.smartevent.user_service.dto.response.UserResponse;
import com.smartevent.user_service.entity.User;
import com.smartevent.user_service.exception.UserNotFoundException;
import com.smartevent.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));
        return UserResponse.fromUser(user);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email
                ));
        return UserResponse.fromUser(user);
    }

    public UserResponse updateUser(String id, UpdateUserRequest request, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));

        // Check if the current user is updating their own profile
        if (!user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You can only update your own profile");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.save(user);
        return UserResponse.fromUser(user);
    }

    public void deleteUser(String id, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));

        // Check if the current user is deleting their own account
        if (!user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You can only delete your own account");
        }

        userRepository.delete(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
}