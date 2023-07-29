package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.Credit;
import ru.ccfit.bozhko.models.PaymentSchedule;
import ru.ccfit.bozhko.repository.PaymentHistoryRepository;
import ru.ccfit.bozhko.repository.PaymentScheduleRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.max;

@Service
@Transactional(readOnly = true)
public class PaymentScheduleService {
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    public PaymentScheduleService(PaymentScheduleRepository paymentScheduleRepository, PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentScheduleRepository = paymentScheduleRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public List<PaymentSchedule> findAll() {
        return paymentScheduleRepository.findAll();
    }

    public PaymentSchedule findOne(int id) {
        Optional<PaymentSchedule> foundPayment = paymentScheduleRepository.findById(id);
        return foundPayment.orElse(null);
    }

    @Transactional
    public void save(PaymentSchedule payment) {
        paymentScheduleRepository.save(payment);
    }

    @Transactional
    public void update(int id, PaymentSchedule updatedPayment) {
        updatedPayment.setId(id);
        paymentScheduleRepository.save(updatedPayment);
    }

    @Transactional
    public void delete(int id) {
        paymentScheduleRepository.deleteById(id);
    }

    @Transactional
    public double getSumLoanCredit(Credit credit){
        return paymentScheduleRepository.getSumLoanCredit(credit);
    }

    @Transactional
    public double getSumLoanByDate(Credit credit, Date date){
        Double sum = paymentScheduleRepository.getSumLoanByDate(credit, date);
        return sum == null ? 0 : sum;
    }

    @Transactional
    public double getSumPercentByDate(Credit credit, Date date){
        Double sum = paymentScheduleRepository.getSumPercentByDate(credit, date);
        return sum == null ? 0 : sum;
    }

    @Transactional
    public Date getNearestDate(Credit credit, Date date){
        Timestamp timestamp = paymentScheduleRepository.getNearestDate(credit, date);
        return new Date(timestamp.getTime());
    }

    @Transactional
    public int getRemainPeriod(Credit credit, Date date){
        return paymentScheduleRepository.getRemainPeriod(credit, date);
    }

    @Transactional
    public List<PaymentSchedule> getCreditPayments(Credit credit){
        return paymentScheduleRepository.getCreditPayments(credit);
    }

    @Transactional
    public List<Map<String, Object>> getPaymentsByDate(Date date){
        return paymentScheduleRepository.getPaymentsByDate(date);
    }

    @Transactional
    public void deleteAllByCredit(Credit credit){
        paymentScheduleRepository.deleteAllByCredit(credit);
    }

    public double getDelayLoan(Credit credit){
        Date currentDate = Date.valueOf(LocalDate.now());

        double sumLoanSchedule = paymentScheduleRepository.getSumLoanByDate(credit, currentDate);
        double sumPaymentsLoan = paymentHistoryRepository.getSumLoanByDate(credit, currentDate);
        return max(0, sumLoanSchedule - sumPaymentsLoan);
    }

    public double getDelayPercent(Credit credit){
        Date currentDate = Date.valueOf(LocalDate.now());

        double sumPercentSchedule = paymentScheduleRepository.getSumPercentByDate(credit, currentDate);
        double sumPaymentsPercent = paymentHistoryRepository.getSumPercentByDate(credit, currentDate);
        return max(0, sumPercentSchedule - sumPaymentsPercent);
    }

}
