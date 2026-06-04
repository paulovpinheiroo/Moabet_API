package com.api.moabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
