package ru.ccfit.bozhko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Integer> {
    Page<Tariff> findAll(Pageable pageable);
}
