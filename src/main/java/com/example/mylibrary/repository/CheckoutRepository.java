package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    List<Checkout> findAllByUserIdOrderByCheckoutDate(Long id);


    Optional<Checkout> findByUserEmailAndBookId(String email, Long bookId);
}
