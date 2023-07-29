package ru.ccfit.bozhko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.models.Credit;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Integer> {
    @Query("SELECT c FROM Credit c WHERE c.client = :client AND c.isActive = true")
    List<Credit> findActiveCreditsByClient(Client client);

    @Query("SELECT c FROM Credit c WHERE c.isActive = true")
    Page<Credit> findActiveCredits(Pageable pageable);

    Page<Credit> findAll(Pageable pageable);

    List<Credit> findAll();

    @Query("SELECT c FROM Credit c WHERE c.client = :client")
    List<Credit> findCreditsByClient(Client client);
}
