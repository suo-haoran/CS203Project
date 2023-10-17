package com.cs203g3.ticketing.auth;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cs203g3.ticketing.security.auth.AuthService;
import com.cs203g3.ticketing.security.auth.EmailTakenException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.security.auth.UserDetailsServiceImpl;
import com.cs203g3.ticketing.security.auth.UsernameTakenException;
import com.cs203g3.ticketing.security.jwt.JwtUtils;
import com.cs203g3.ticketing.security.jwt.dto.JwtResponse;
import com.cs203g3.ticketing.user.ERole;
import com.cs203g3.ticketing.user.Role;
import com.cs203g3.ticketing.user.RoleRepository;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;
import com.cs203g3.ticketing.user.dto.LoginRequest;
import com.cs203g3.ticketing.user.dto.SignupRequest;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository users;

    @Mock
    private RoleRepository roles;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private final Role ADMIN = new Role(1L, ERole.ROLE_ADMIN, null);

    private final User USER = new User(
            1L, "username", "valid_password", "ticketingwinners@gmail.com", "12312312", "SG", new Date(),
            Set.of(ADMIN));

    @Test
    public void login_Valid_Success() {
        // Setup LoginRequest and Mock Authentication object
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUsername("username");
        loginRequest.setPassword("valid_password");

        String expectedToken = "jwtString";
        Authentication authentication = mock(Authentication.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());

        // Return the mock authentication object when
        // authenticationManager.authenticate() is called (since we don't know what the
        // actual authentication object is)
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        // Return the mock user object when authentication.getPrincipal() is called
        // (needed to construct JwtResponse)
        when(authentication.getPrincipal()).thenReturn(UserDetailsImpl.build(USER));

        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

        assertTrue(jwtResponse.getToken().equals(expectedToken));
        verify(authenticationManager).authenticate(usernamePasswordAuthenticationToken);
        verify(jwtUtils).generateJwtToken(authentication);
        verify(authentication).getPrincipal();
    }

    @Test
    public void login_InvalidCredentials_Failure() {
        // Setup invalid LoginRequest
        LoginRequest loginRequest = new LoginRequest();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());

        // If LoginRequest is invalid, authenticationManager.authenticate() will throw
        // BadCredentialsException
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenThrow(BadCredentialsException.class);

        // Verify that authService.authenticateUser() throws BadCredentialsException
        assertThrowsExactly(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });
        verify(authenticationManager).authenticate(usernamePasswordAuthenticationToken);
    }

    @Test
    public void signup_Valid_Success() {
        // Setup valid SignupRequest
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("valid_new_username");
        signupRequest.setPassword("valid_new_password");
        signupRequest.setRoles(Set.of("admin"));

        when(users.existsByUsername(signupRequest.getUsername())).thenReturn(false);
        when(users.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roles.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(ADMIN));
        when(users.save(any(User.class))).thenReturn(USER);

        User user = authService.registerUser(signupRequest);
        assertTrue(user.getId().equals(USER.getId()));
        verify(users).existsByUsername(signupRequest.getUsername());
        verify(users).existsByEmail(signupRequest.getEmail());
        verify(roles).findByName(ERole.ROLE_ADMIN);
        verify(users).save(any(User.class));
    }

    @Test
    public void signup_UsernameExists_Failure() {
        // Setup invalid username (already exists)
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("duplicate_username");

        // Assume that username already exists
        when(users.existsByUsername(signupRequest.getUsername())).thenReturn(true);

        // Verify that authService.registerUser() throws UsernameTakenException
        assertThrowsExactly(UsernameTakenException.class, () -> {
            authService.registerUser(signupRequest);
        });
        verify(users).existsByUsername(signupRequest.getUsername());
    }

    @Test
    public void signup_EmailExists_Failure() {
        // Setup invalid email (already exists)
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("duplicate_email@gmail.com");

        // Assume that username does not exist
        when(users.existsByUsername(signupRequest.getUsername())).thenReturn(false);
        // Assume that email already exists
        when(users.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // Verify that authService.registerUser() throws EmailTakenException
        assertThrowsExactly(EmailTakenException.class, () -> {
            authService.registerUser(signupRequest);
        });
        verify(users).existsByUsername(signupRequest.getUsername());
        verify(users).existsByEmail(signupRequest.getEmail());

    }

    @Test
    public void signup_NoRole_Failure() {
        // Setup invalid role (null)
        SignupRequest signupRequest = new SignupRequest();

        // Assume that username does not exist
        when(users.existsByUsername(signupRequest.getUsername())).thenReturn(false);
        // Assume that email already exists
        when(users.existsByEmail(signupRequest.getEmail())).thenReturn(false);

        // Verify that authService.registerUser() throws IllegalArgumentException
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            authService.registerUser(signupRequest);
        });
        verify(users).existsByUsername(signupRequest.getUsername());
        verify(users).existsByEmail(signupRequest.getEmail());

    }
}
