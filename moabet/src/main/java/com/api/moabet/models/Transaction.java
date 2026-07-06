package com.api.moabet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api.moabet.models.enums.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Transaction")
@Table(name = "Transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction", nullable = false, unique = true)
    private Long id;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "id_wallet", nullable = false)
    private Wallet wallet;
}
