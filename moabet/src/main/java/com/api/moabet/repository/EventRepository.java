package com.api.moabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
