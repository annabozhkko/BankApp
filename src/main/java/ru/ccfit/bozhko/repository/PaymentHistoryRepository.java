package ru.ccfit.bozhko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.Credit;
import ru.ccfit.bozhko.models.PaymentHistory;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {
    @Query("SELECT SUM(p.sumLoan) FROM PaymentHistory p WHERE p.credit = :credit AND p.date <= :date")
    Double getSumLoanByDate(Credit credit, Date date);

    @Query("SELECT SUM(p.sumPercent) FROM PaymentHistory p WHERE p.credit = :credit AND p.date <= :date")
    Double getSumPercentByDate(Credit credit, Date date);

    @Query("SELECT p FROM PaymentHistory p WHERE p.credit = :credit")
    Page<PaymentHistory> findAllByCredit(Credit credit, Pageable pageable);

    @Query("SELECT p.credit AS credit, SUM(p.sumLoan + p.sumPercent) AS sum FROM PaymentHistory p WHERE p.date <= :date GROUP BY p.credit")
    List<Map<String, Object>> getPaymentsByDate(Date date);

    void deleteAllByCredit(Credit credit);
}
