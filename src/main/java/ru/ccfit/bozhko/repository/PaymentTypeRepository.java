package ru.ccfit.bozhko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.PaymentType;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
}
