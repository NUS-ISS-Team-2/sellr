package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByIdentifierAcrossCollections_ShouldReturnBuyer() {
        String identifier = "buyer@example.com";
        Buyer buyer = new Buyer("buyer1", identifier, "pw", 0);

        when(mongoTemplate.findOne(any(Query.class), eq(Buyer.class), eq("buyers")))
                .thenReturn(buyer);
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins")))
                .thenReturn(null);
        when(mongoTemplate.findOne(any(Query.class), eq(Seller.class), eq("sellers")))
                .thenReturn(null);

        Optional<User> result = userRepository.findByIdentifierAcrossCollections(identifier);

        assertTrue(result.isPresent());
        assertEquals(buyer, result.get());
    }

    @Test
    void findByIdentifierAcrossCollections_ShouldReturnEmpty_WhenNotFound() {
        when(mongoTemplate.findOne(any(Query.class), any(Class.class), anyString()))
                .thenReturn(null);

        Optional<User> result = userRepository.findByIdentifierAcrossCollections("unknown@example.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdentifierAcrossCollections_ShouldReturnEmpty_WhenNull() {
        when(mongoTemplate.findOne(any(Query.class), any(Class.class), anyString()))
                .thenReturn(null);

        Optional<User> result = userRepository.findByIdentifierAcrossCollections(null);
        assertTrue(result.isEmpty());
    }
}
