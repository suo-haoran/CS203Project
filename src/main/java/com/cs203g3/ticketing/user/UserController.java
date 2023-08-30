package com.cs203g3.ticketing.user;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserRepository users;
    private BCryptPasswordEncoder encoder;
    private CustomUserDetailsService service;

    public UserController(UserRepository users, BCryptPasswordEncoder encoder, CustomUserDetailsService service){
        this.users = users;
        this.encoder = encoder;
        this.service = service;
    }

    @GetMapping
    public List<User> getUsers() {
        return users.findAll();
    }

    @PostMapping("/login")
    public UserDetails login(@RequestBody Credentials cred) {
        cred.password = encoder.encode(cred.password);
        return service.login(cred);
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/signup")
    public User addUser(@RequestBody User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }
}
