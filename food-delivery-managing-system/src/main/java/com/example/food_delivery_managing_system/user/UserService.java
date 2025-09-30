package com.example.food_delivery_managing_system.user;

import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void addOwner(UserRequest userRequest) {
        String encodedPassword = bCryptPasswordEncoder.encode(userRequest.getPassword());
        User user = userRepository.save(userRequest.toEntity(encodedPassword));
        UserResponse.from(user);
    }

    public boolean existsByEmail(String email) {
        boolean checkEmail = userRepository.existsByEmail(email);
        System.out.println("★★★★★★★★★★ 이메일 체크 =" + checkEmail);
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickName(String nickName) {
        boolean checkNickName = userRepository.existsByNickName(nickName);
        System.out.println("★★★★★★★★★★ 닉네임 체크 =" + checkNickName);
        return userRepository.existsByNickName(nickName);
    }
}
