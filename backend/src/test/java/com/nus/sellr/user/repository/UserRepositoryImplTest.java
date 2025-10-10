package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private UserRepositoryImpl repo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repo = new UserRepositoryImpl();
        repo.mongoTemplate = mongoTemplate;
    }

    // -------------------- findByIdentifierAcrossCollections --------------------
    @Test
    void testFindByIdentifier_nullOrBlank() {
        assertEquals(Optional.empty(), repo.findByIdentifierAcrossCollections(null));
        assertEquals(Optional.empty(), repo.findByIdentifierAcrossCollections(""));
        assertEquals(Optional.empty(), repo.findByIdentifierAcrossCollections("   "));
    }

    @Test
    void testFindByIdentifier_foundInAdmin() {
        Admin admin = new Admin();
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(admin);

        Optional<User> result = repo.findByIdentifierAcrossCollections("identifier");
        assertTrue(result.isPresent());
        assertEquals(admin, result.get());

        // Seller and Buyer should not be called if Admin is found
        verify(mongoTemplate, never()).findOne(any(Query.class), eq(Seller.class), eq("sellers"));
        verify(mongoTemplate, never()).findOne(any(Query.class), eq(Buyer.class), eq("buyers"));
    }

    @Test
    void testFindByIdentifier_foundInSeller() {
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(null);
        Seller seller = new Seller();
        when(mongoTemplate.findOne(any(Query.class), eq(Seller.class), eq("sellers"))).thenReturn(seller);

        Optional<User> result = repo.findByIdentifierAcrossCollections("identifier");
        assertTrue(result.isPresent());
        assertEquals(seller, result.get());
    }

    @Test
    void testFindByIdentifier_foundInBuyer() {
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(null);
        when(mongoTemplate.findOne(any(Query.class), eq(Seller.class), eq("sellers"))).thenReturn(null);
        Buyer buyer = new Buyer();
        when(mongoTemplate.findOne(any(Query.class), eq(Buyer.class), eq("buyers"))).thenReturn(buyer);

        Optional<User> result = repo.findByIdentifierAcrossCollections("identifier");
        assertTrue(result.isPresent());
        assertEquals(buyer, result.get());
    }

    @Test
    void testFindByIdentifier_notFound() {
        when(mongoTemplate.findOne(any(Query.class), any(), anyString())).thenReturn(null);
        Optional<User> result = repo.findByIdentifierAcrossCollections("identifier");
        assertTrue(result.isEmpty());
    }

    // -------------------- findAllUsers --------------------
    @Test
    void testFindAllUsers() {
        Admin admin = new Admin();
        Seller seller = new Seller();
        Buyer buyer = new Buyer();

        when(mongoTemplate.findAll(Admin.class, "admins")).thenReturn(List.of(admin));
        when(mongoTemplate.findAll(Seller.class, "sellers")).thenReturn(List.of(seller));
        when(mongoTemplate.findAll(Buyer.class, "buyers")).thenReturn(List.of(buyer));

        List<User> users = repo.findAllUsers();
        assertEquals(3, users.size());
        assertTrue(users.contains(admin));
        assertTrue(users.contains(seller));
        assertTrue(users.contains(buyer));
    }

    // -------------------- findById --------------------
    @Test
    void testFindById_foundInAdmin() {
        Admin admin = new Admin();
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(admin);
        Optional<User> result = repo.findById("id1");
        assertTrue(result.isPresent());
        assertEquals(admin, result.get());
    }

    @Test
    void testFindById_foundInSeller() {
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(null);
        Seller seller = new Seller();
        when(mongoTemplate.findOne(any(Query.class), eq(Seller.class), eq("sellers"))).thenReturn(seller);

        Optional<User> result = repo.findById("id1");
        assertTrue(result.isPresent());
        assertEquals(seller, result.get());
    }

    @Test
    void testFindById_foundInBuyer() {
        when(mongoTemplate.findOne(any(Query.class), eq(Admin.class), eq("admins"))).thenReturn(null);
        when(mongoTemplate.findOne(any(Query.class), eq(Seller.class), eq("sellers"))).thenReturn(null);
        Buyer buyer = new Buyer();
        when(mongoTemplate.findOne(any(Query.class), eq(Buyer.class), eq("buyers"))).thenReturn(buyer);

        Optional<User> result = repo.findById("id1");
        assertTrue(result.isPresent());
        assertEquals(buyer, result.get());
    }

    @Test
    void testFindById_notFound() {
        when(mongoTemplate.findOne(any(Query.class), any(), anyString())).thenReturn(null);
        Optional<User> result = repo.findById("id1");
        assertTrue(result.isEmpty());
    }
}
