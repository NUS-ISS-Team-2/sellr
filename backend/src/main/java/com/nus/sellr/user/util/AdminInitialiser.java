package com.nus.sellr.user.util;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminInitialiser {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminInitialiser(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostConstruct
    public void initAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin(
                    "admin",
                    "admin@admin.com",
                    PasswordUtil.hashPassword("admin")
            );
            adminRepository.save(admin);
            System.out.println("Default admin account created");
        }
    }
}