package ru.ccfit.bozhko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.BlockedClient;
import ru.ccfit.bozhko.models.Client;

import java.util.Date;
import java.util.List;

@Repository
public interface BlockedClientRepository extends JpaRepository<BlockedClient, Integer> {
    @Query("SELECT c FROM BlockedClient c WHERE c.client = :client AND c.date1 >= :date AND c.date2 <= :date")
    List<BlockedClient> findByClient(Client client, Date date);
}

