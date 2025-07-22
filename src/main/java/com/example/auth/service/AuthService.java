package com.example.auth.service;

import com.example.auth.dto.JwtAuthDto;
import com.example.auth.dto.RefreshTokenDto;
import com.example.auth.dto.UserCredentialsDto;
import com.example.auth.jwt.JwtService;
import com.example.auth.model.MyUser;
import com.example.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void addUser(MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRefreshToken("");
        repository.save(user);
    }

    public JwtAuthDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        MyUser user = findByCredentials(userCredentialsDto);

        JwtAuthDto jwtAuthDto = jwtService.generateAuthToken(user.getName());
        
        user.setRefreshToken(jwtAuthDto.getRefreshToken());
        repository.save(user);

        return jwtAuthDto;
    }

    public JwtAuthDto refreshToken(RefreshTokenDto refreshTokenDto) throws AuthenticationException {
        String oldRefreshToken = refreshTokenDto.getRefreshToken();

        if (oldRefreshToken == null || !jwtService.validateJwtToken(oldRefreshToken)) {
            throw new AuthenticationException("Invalid refresh token");
        }

        String username = jwtService.getUsernameFromToken(oldRefreshToken);
        MyUser user = findByUsername(username);

        if (!oldRefreshToken.equals(user.getRefreshToken())) {
            throw new AuthenticationException("Refresh token mismatch");
        }

        JwtAuthDto jwtAuthDto = jwtService.generateAuthToken(username);
        user.setRefreshToken(jwtAuthDto.getRefreshToken());
        repository.save(user);

        return jwtAuthDto;
    }

    private MyUser findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<MyUser> optUser = repository.findByName(userCredentialsDto.getUsername());
        if (optUser.isPresent()) {
            MyUser user = optUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("Invalid credentials");
    }

    public MyUser findByUsername(String username) throws UsernameNotFoundException {
        return repository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username + " notfound"));
    }
}
