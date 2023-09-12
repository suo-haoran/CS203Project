package com.cs203g3.ticketing.receipt;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.cs203g3.ticketing.user.User;


public interface ReceiptRepository extends JpaRepository<Receipt, UUID>{
    List<Receipt> findByUser(User user);
}
