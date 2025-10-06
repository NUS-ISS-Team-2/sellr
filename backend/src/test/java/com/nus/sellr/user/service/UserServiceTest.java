package com.nus.sellr.user.service;

import com.nus.sellr.user.dto.*;
import com.nus.sellr.user.entity.*;
import com.nus.sellr.user.factory.UserFactory;
import com.nus.sellr.user.repository.*;
import com.nus.sellr.user.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.nus.sellr.user.entity.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private BuyerRepository buyerRepository;
    @Mock private SellerRepository sellerRepository;
    @Mock private AdminRepository adminRepository;
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
        request.setRole(BUYER);

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
        request.setRole(BUYER);

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
        request.setRole(BUYER);

        Buyer buyer = new Buyer("buyer1", "buyer1@example.com", "hashedpw", 0);
        buyer.setId("id123");

        when(buyerRepository.existsByUsername("buyer1")).thenReturn(false);
        when(sellerRepository.existsByUsername("buyer1")).thenReturn(false);
        when(buyerRepository.existsByEmail("buyer1@example.com")).thenReturn(false);
        when(sellerRepository.existsByEmail("buyer1@example.com")).thenReturn(false);

        when(userFactory.createUser(anyString(), anyString(), anyString(), eq(BUYER))).thenReturn(buyer);
        when(buyerRepository.save(buyer)).thenReturn(buyer);

        CreateUserResponse response = userService.createUser(request);

        assertEquals("id123", response.getId());
        assertEquals("buyer1", response.getUsername());
        assertEquals("buyer1@example.com", response.getEmail());
    }

    @Test
    void createUser_ShouldSaveSeller() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("seller1");
        request.setEmail("seller1@example.com");
        request.setPassword("pw");
        request.setRole(SELLER);

        Seller seller = new Seller("seller1", "seller1@example.com", "hashedpw", "My New Store");
        seller.setId("id456");

        // Mock existence checks
        when(buyerRepository.existsByUsername("seller1")).thenReturn(false);
        when(sellerRepository.existsByUsername("seller1")).thenReturn(false);
        when(buyerRepository.existsByEmail("seller1@example.com")).thenReturn(false);
        when(sellerRepository.existsByEmail("seller1@example.com")).thenReturn(false);

        // Mock user factory and save
        when(userFactory.createUser(anyString(), anyString(), anyString(), eq(SELLER)))
                .thenReturn(seller);
        when(sellerRepository.save(seller)).thenReturn(seller);

        // Act
        CreateUserResponse response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("id456", response.getId());
        assertEquals("seller1", response.getUsername());
        assertEquals("seller1@example.com", response.getEmail());

        // Verify interactions
        verify(buyerRepository).existsByUsername("seller1");
        verify(sellerRepository).existsByUsername("seller1");
        verify(buyerRepository).existsByEmail("seller1@example.com");
        verify(sellerRepository).existsByEmail("seller1@example.com");
        verify(userFactory).createUser(anyString(), anyString(), anyString(), eq(SELLER));
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

    // --------------------------
    // getAllUsers()
    // --------------------------
    @Test
    void testGetAllUsers_variousRoles() {
        Admin admin = new Admin(); admin.setId("a1"); admin.setUsername("admin"); admin.setEmail("a@a.com");
        Buyer buyer = new Buyer(); buyer.setId("b1"); buyer.setUsername("buyer"); buyer.setEmail("b@b.com");
        Seller seller = new Seller(); seller.setId("s1"); seller.setUsername("seller"); seller.setEmail("s@s.com");
        User unknown = new User() {{ setId("u1"); setUsername("u"); setEmail("u@u.com"); }};

        when(userRepository.findAllUsers()).thenReturn(Arrays.asList(admin, buyer, seller, unknown));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(4, responses.size());
        assertEquals(ADMIN, responses.get(0).getRole());
        assertEquals(BUYER, responses.get(1).getRole());
        assertEquals(SELLER, responses.get(2).getRole());
        assertEquals(UNKNOWN, responses.get(3).getRole());
    }

    // --------------------------
    // updateUser()
    // --------------------------
    @Test
    void testUpdateUser_success() {
        String userId = "u1";
        Buyer buyer = new Buyer();
        buyer.setId(userId);
        buyer.setUsername("oldName");
        buyer.setEmail("old@example.com");

        UpdateUser dto = new UpdateUser();
        dto.setUsername("newName");
        dto.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(buyer));
        doAnswer(invocation -> null).when(buyerRepository).save(any(Buyer.class));

        UserResponse response = userService.updateUser(userId, dto);

        assertEquals("newName", response.getUsername());
        assertEquals("new@example.com", response.getEmail());
        assertEquals(BUYER, response.getRole());
        verify(buyerRepository).save(buyer);
    }

    @Test
    void testUpdateUser_notFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        UpdateUser dto = new UpdateUser();
        assertThrows(RuntimeException.class, () -> userService.updateUser("u1", dto));
    }

    // --------------------------
    // changePassword()
    // --------------------------
    @Test
    void testChangePassword_success() {
        Buyer buyer = new Buyer();
        buyer.setId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(buyer));
        doAnswer(invocation -> null).when(buyerRepository).save(any(Buyer.class));

        userService.changePassword("u1", "newPassword");

        assertNotNull(buyer.getPassword());
        assertNotEquals("newPassword", buyer.getPassword()); // hashed
        verify(buyerRepository).save(buyer);
    }

    @Test
    void testChangePassword_userNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.changePassword("u1", "pwd"));
    }

    // --------------------------
    // deleteUser()
    // --------------------------
    @Test
    void testDeleteUser_admin() {
        Admin admin = new Admin();
        admin.setId("a1");
        when(userRepository.findById("a1")).thenReturn(Optional.of(admin));
        doNothing().when(adminRepository).delete(admin);

        userService.deleteUser("a1");
        verify(adminRepository).delete(admin);
    }

    @Test
    void testDeleteUser_seller() {
        Seller seller = new Seller();
        seller.setId("s1");
        when(userRepository.findById("s1")).thenReturn(Optional.of(seller));
        doNothing().when(sellerRepository).delete(seller);

        userService.deleteUser("s1");
        verify(sellerRepository).delete(seller);
    }

    @Test
    void testDeleteUser_buyer() {
        Buyer buyer = new Buyer();
        buyer.setId("b1");
        when(userRepository.findById("b1")).thenReturn(Optional.of(buyer));
        doNothing().when(buyerRepository).delete(buyer);

        userService.deleteUser("b1");
        verify(buyerRepository).delete(buyer);
    }

    @Test
    void testDeleteUser_notFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deleteUser("u1"));
    }

    // --------------------------
    // saveUser()
    // --------------------------
    @Test
    void testSaveUser_roles() {
        // Create real entity instances
        Seller seller = new Seller();
        Buyer buyer = new Buyer();
        Admin admin = new Admin();

        // Mock repository save methods (they are void)
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        // Assertions
        assertEquals(SELLER, userService.saveUser(seller));
        assertEquals(BUYER, userService.saveUser(buyer));
        assertEquals(ADMIN, userService.saveUser(admin));
        assertEquals(UNKNOWN, userService.saveUser(null));
    }

}
