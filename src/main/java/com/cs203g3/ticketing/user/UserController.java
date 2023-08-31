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
    private UserService service;

    public UserController(UserRepository users,UserService service){
        this.users = users;
        this.service = service;
    }

    @GetMapping
    public List<User> getUsers() {
        return users.findAll();
    }

    @PostMapping("/auth/login")
    public User login(@RequestBody Credentials cred) {
        return service.login(cred);
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/auth/signup")
    public User addUser(@RequestBody User user){
        return service.addUser(user);
    }
}
