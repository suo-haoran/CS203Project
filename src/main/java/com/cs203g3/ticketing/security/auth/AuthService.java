package com.cs203g3.ticketing.security.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.dto.MessageResponse;
import com.cs203g3.ticketing.security.jwt.JwtUtils;
import com.cs203g3.ticketing.security.jwt.dto.JwtResponse;
import com.cs203g3.ticketing.user.ERole;
import com.cs203g3.ticketing.user.Role;
import com.cs203g3.ticketing.user.RoleRepository;
import com.cs203g3.ticketing.user.RoleService;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;
import com.cs203g3.ticketing.user.dto.LoginRequest;
import com.cs203g3.ticketing.user.dto.SignupRequest;

@Service
public class AuthService {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder encoder;
    private JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleService roleService, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Authenticate a user
     * @param loginRequest contains the user's credentials
     * @return a JwtResponse containing the JWT token and user details
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    /**
     * Register a new user
     * 
     * @param signUpRequest contains the user's details
     * @return the newly created user
     */
    public User registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameTakenException(signUpRequest.getUsername());
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailTakenException(signUpRequest.getEmail());
        }

        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }

        // get role objects from role names.
        Set<Role> roles = signUpRequest.getRoles().stream()
                .map(roleService::getRoleByName)
                .collect(Collectors.toSet());

        return userRepository.save(new User(
                null,
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail(),
                signUpRequest.getPhone(),
                signUpRequest.getCountryOfResidences(),
                signUpRequest.getDob(),
                roles));
    }

}
