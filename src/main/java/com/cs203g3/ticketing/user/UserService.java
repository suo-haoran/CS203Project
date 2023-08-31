package com.cs203g3.ticketing.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository users;
    private BCryptPasswordEncoder encoder;

    public UserService(UserRepository users, BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
        this.users = users;
    }

    public User login(Credentials credentials) {
        User user = users.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (encoder.matches(credentials.getPassword(), user.getPassword())) {
            return user;
        }

        return null;
    }

    public User addUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }
}
