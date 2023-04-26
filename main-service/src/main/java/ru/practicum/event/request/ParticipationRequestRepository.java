package ru.practicum.event.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.request.model.ParticipationRequest;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>, QuerydslPredicateExecutor<ParticipationRequest> {
}