package ru.ccfit.bozhko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ccfit.bozhko.models.Credit;
import ru.ccfit.bozhko.models.PaymentSchedule;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Integer> {
    @Query("SELECT SUM(p.sumLoan) FROM PaymentSchedule p WHERE p.credit = :credit")
    Double getSumLoanCredit(Credit credit);

    @Query("SELECT SUM(p.sumPercent) FROM PaymentSchedule p WHERE p.credit = :credit")
    Double getSumPercentCredit(Credit credit);

    @Query("SELECT SUM(p.sumLoan) FROM PaymentSchedule p WHERE p.credit = :credit AND p.date <= :date")
    Double getSumLoanByDate(Credit credit, Date date);

    @Query("SELECT SUM(p.sumPercent) FROM PaymentSchedule p WHERE p.credit = :credit AND p.date <= :date")
    Double getSumPercentByDate(Credit credit, Date date);

    @Query("SELECT MIN(p.date) FROM PaymentSchedule p WHERE p.credit = :credit AND p.date > :date")
    Timestamp getNearestDate(Credit credit, Date date);

    @Query("SELECT p FROM PaymentSchedule p WHERE p.credit = :credit AND p.date >= :date")
    List<PaymentSchedule> getPaymentsAfterDate(Credit credit, Date date);

    @Query("SELECT COUNT(p) FROM PaymentSchedule p WHERE p.credit = :credit AND p.date >= :date")
    Integer getRemainPeriod(Credit credit, Date date);

    @Query("SELECT p FROM PaymentSchedule p WHERE p.credit = :credit")
    List<PaymentSchedule> getCreditPayments(Credit credit);

    @Query("SELECT p.credit AS credit, SUM(p.sumLoan + p.sumPercent) AS sum FROM PaymentSchedule p WHERE p.date <= :date GROUP BY p.credit ORDER BY p.credit")
    List<Map<String, Object>> getPaymentsByDate(Date date);

    void deleteAllByCredit(Credit credit);

    @Query("DELETE FROM PaymentHistory p WHERE p.credit = :credit AND p.date >= :date")
    void deletePaymentsAfterDate(Credit credit, Date date);

}
