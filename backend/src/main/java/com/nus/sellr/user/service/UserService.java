package com.nus.sellr.user.service;

import com.nus.sellr.user.dto.CreateUserRequest;
import com.nus.sellr.user.dto.CreateUserResponse;
import com.nus.sellr.user.entity.*;
import com.nus.sellr.user.factory.UserFactory;
import com.nus.sellr.user.repository.AdminRepository;
import com.nus.sellr.user.repository.BuyerRepository;
import com.nus.sellr.user.repository.SellerRepository;
import com.nus.sellr.user.util.PasswordUtil;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final UserFactory userFactory;

    public UserService(AdminRepository adminRepository,
                       BuyerRepository buyerRepository,
                       SellerRepository sellerRepository,
                       UserFactory userFactory) {
        this.adminRepository = adminRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.userFactory = userFactory;
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        Role role = request.getRole();

        User user = userFactory.createUser(
                request.getUsername(),
                request.getEmail(),
                PasswordUtil.hashPassword(request.getPassword()),
                role
        );

        User savedUser;
        switch (role) {
            case ADMIN:
                savedUser = adminRepository.save((Admin) user);
                break;
            case BUYER:
                savedUser = buyerRepository.save((Buyer) user);
                break;
            case SELLER:
                savedUser = sellerRepository.save((Seller) user);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        return new CreateUserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
