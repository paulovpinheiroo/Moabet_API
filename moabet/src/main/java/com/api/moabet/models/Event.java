package com.api.moabet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api.moabet.models.enums.Result;
import com.api.moabet.models.enums.StatusEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "event")
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event", nullable = false, unique = true)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "odds", nullable = false)
    private BigDecimal odds;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEvent status;
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private Result result;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
