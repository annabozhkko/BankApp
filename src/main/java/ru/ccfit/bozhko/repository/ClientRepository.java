package ru.ccfit.bozhko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Page<Client> findAll(Pageable pageable);

    List<Client> findAll();
}

