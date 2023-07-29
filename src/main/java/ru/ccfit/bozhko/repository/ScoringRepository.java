package ru.ccfit.bozhko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.models.Scoring;

import java.util.List;

@Repository
public interface ScoringRepository extends JpaRepository<Scoring, Integer> {
    @Query("SELECT s FROM Scoring s WHERE s.client = :client AND s.approvalStatus = false")
    List<Scoring> findByClient(Client client);

    Page<Scoring> findAll(Pageable pageable);
}
