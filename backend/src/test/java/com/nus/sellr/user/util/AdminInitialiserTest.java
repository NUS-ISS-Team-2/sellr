package com.nus.sellr.user.util;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class AdminInitialiserTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminInitialiser adminInitialiser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitAdmin_createsAdmin_whenNoneExist() {
        // Arrange
        when(adminRepository.count()).thenReturn(0L);

        // Act
        adminInitialiser.initAdmin();

        // Assert
        verify(adminRepository, times(1)).save(Mockito.any(Admin.class));
    }

    @Test
    void testInitAdmin_doesNotCreateAdmin_whenAlreadyExists() {
        // Arrange
        when(adminRepository.count()).thenReturn(1L);

        // Act
        adminInitialiser.initAdmin();

        // Assert
        verify(adminRepository, never()).save(any());
    }
}
