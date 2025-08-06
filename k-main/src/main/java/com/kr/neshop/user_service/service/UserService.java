package com.kr.neshop.user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kr.neshop.user_service.DTO.UserDTO;
import com.kr.neshop.user_service.model.Role;
import com.kr.neshop.user_service.model.User;
import com.kr.neshop.user_service.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet()));
        return dto;
    }
    public List<UserDTO> getUsersByRole(String roleName) {
        List<User> allUsers = userRepository.findAll();

        // Debugging output (optional)
        for (User user : allUsers) {
            System.out.println("User: " + user.getUsername());
            for (Role role : user.getRoles()) {
                System.out.println(" -> Role: " + role.getName().name());
            }
        }

        return allUsers.stream()
            .filter(user -> user.getRoles().stream()
                .anyMatch(role -> role.getName().name().toUpperCase().contains(roleName.toUpperCase()))
            )
            .map(user -> {
                UserDTO dto = new UserDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setPhoneNumber(user.getPhoneNumber());
                dto.setRoles(user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toSet()));
                return dto;
            })
            .collect(Collectors.toList());
    }



}
