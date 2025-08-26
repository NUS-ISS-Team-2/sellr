package com.nus.sellr.user.service;

import com.nus.sellr.user.dto.CreateUserRequest;
import com.nus.sellr.user.dto.CreateUserResponse;
import com.nus.sellr.user.dto.LoginRequest;
import com.nus.sellr.user.dto.LoginResponse;
import com.nus.sellr.user.entity.*;
import com.nus.sellr.user.factory.UserFactory;
import com.nus.sellr.user.repository.*;
import com.nus.sellr.user.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private BuyerRepository buyerRepository;
    @Mock private SellerRepository sellerRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserFactory userFactory;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------
    // createUser() tests
    // -------------------

    @Test
    void createUser_ShouldThrow_WhenUsernameExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setRole(Role.BUYER);

        when(buyerRepository.existsByUsername("john")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Username exists.", ex.getMessage());
    }

    @Test
    void createUser_ShouldThrow_WhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("jane");
        request.setEmail("jane@example.com");
        request.setRole(Role.BUYER);

        when(buyerRepository.existsByUsername("jane")).thenReturn(false);
        when(sellerRepository.existsByUsername("jane")).thenReturn(false);
        when(buyerRepository.existsByEmail("jane@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Email exists.", ex.getMessage());
    }

    @Test
    void createUser_ShouldSaveBuyer() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("buyer1");
        request.setEmail("buyer1@example.com");
        request.setPassword("pw");
        request.setRole(Role.BUYER);

        Buyer buyer = new Buyer("buyer1", "buyer1@example.com", "hashedpw", 0);
        buyer.setId("id123");

        when(buyerRepository.existsByUsername("buyer1")).thenReturn(false);
        when(sellerRepository.existsByUsername("buyer1")).thenReturn(false);
        when(buyerRepository.existsByEmail("buyer1@example.com")).thenReturn(false);
        when(sellerRepository.existsByEmail("buyer1@example.com")).thenReturn(false);

        when(userFactory.createUser(anyString(), anyString(), anyString(), eq(Role.BUYER))).thenReturn(buyer);
        when(buyerRepository.save(buyer)).thenReturn(buyer);

        CreateUserResponse response = userService.createUser(request);

        assertEquals("id123", response.getId());
        assertEquals("buyer1", response.getUsername());
        assertEquals("buyer1@example.com", response.getEmail());
    }

    @Test
    void createUser_ShouldThrow_WhenInvalidRole() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("badrole");
        request.setEmail("bad@example.com");
        request.setPassword("pw");
        request.setRole(null);

        when(buyerRepository.existsByUsername(anyString())).thenReturn(false);
        when(sellerRepository.existsByUsername(anyString())).thenReturn(false);
        when(buyerRepository.existsByEmail(anyString())).thenReturn(false);
        when(sellerRepository.existsByEmail(anyString())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertTrue(ex.getMessage().contains("Role is invalid or null"));
    }

    // -------------------
    // loginUser() tests
    // -------------------

    @Test
    void loginUser_ShouldReturnResponse_WhenPasswordMatches() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user1");
        loginRequest.setPassword("pw");

        User user = new Buyer("user1", "user1@example.com",
                new BCryptPasswordEncoder().encode("pw"), 0);
        user.setId("id123");

        when(userRepository.findByIdentifierAcrossCollections("user1")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("token123");

        LoginResponse response = userService.loginUser(loginRequest);

        assertEquals("id123", response.getId());
        assertEquals("user1", response.getUsername());
        assertEquals("user1@example.com", response.getEmail());
        assertEquals("token123", response.getToken());
    }

    @Test
    void loginUser_ShouldReturnEmpty_WhenUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("nouser");
        loginRequest.setPassword("pw");

        when(userRepository.findByIdentifierAcrossCollections("nouser")).thenReturn(Optional.empty());

        LoginResponse response = userService.loginUser(loginRequest);

        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getToken());
    }

    @Test
    void loginUser_ShouldReturnEmpty_WhenPasswordDoesNotMatch() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user2");
        loginRequest.setPassword("wrongpw");

        User user = new Buyer("user2", "user2@example.com",
                new BCryptPasswordEncoder().encode("correctpw"), 0);

        when(userRepository.findByIdentifierAcrossCollections("user2")).thenReturn(Optional.of(user));

        LoginResponse response = userService.loginUser(loginRequest);

        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getToken());
    }

    // -------------------
    // existence checks
    // -------------------

    @Test
    void usernameExist_ShouldCheckAcrossBuyerAndSeller() {
        when(buyerRepository.existsByUsername("buyerX")).thenReturn(false);
        when(sellerRepository.existsByUsername("buyerX")).thenReturn(true);

        assertTrue(userService.usernameExist("buyerX"));
    }

    @Test
    void emailExist_ShouldCheckAcrossBuyerAndSeller() {
        when(buyerRepository.existsByEmail("buyer@example.com")).thenReturn(false);
        when(sellerRepository.existsByEmail("buyer@example.com")).thenReturn(true);

        assertTrue(userService.emailExist("buyer@example.com"));
    }
}
