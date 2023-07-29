package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.*;
import ru.ccfit.bozhko.repository.CreditRepository;
import ru.ccfit.bozhko.repository.PaymentHistoryRepository;
import ru.ccfit.bozhko.repository.PaymentScheduleRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
@Transactional(readOnly = true)
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final CreditRepository creditRepository;

    @Autowired
    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository, PaymentScheduleRepository paymentScheduleRepository, CreditRepository creditRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentScheduleRepository = paymentScheduleRepository;
        this.creditRepository = creditRepository;
    }

    public List<PaymentHistory> findAll() {
        return paymentHistoryRepository.findAll();
    }

    public PaymentHistory findOne(int id) {
        Optional<PaymentHistory> foundPayment = paymentHistoryRepository.findById(id);
        return foundPayment.orElse(null);
    }

    @Transactional
    public void save(PaymentHistory payment) {
        paymentHistoryRepository.save(payment);
    }

    @Transactional
    public void update(int id, PaymentHistory updatedPayment) {
        updatedPayment.setId(id);
        paymentHistoryRepository.save(updatedPayment);
    }

    @Transactional
    public void delete(int id) {
        paymentHistoryRepository.deleteById(id);
    }

    @Transactional
    public Page<PaymentHistory> findAllByCredit(Credit credit, Pageable pageable){
        return paymentHistoryRepository.findAllByCredit(credit, pageable);
    }

    @Transactional
    public void deleteAllByCredit(Credit credit){
        paymentHistoryRepository.deleteAllByCredit(credit);
    }

    public void createPayment(Credit credit, Date date, int sum, PaymentType paymentType){
        Tariff tariff = credit.getTariff();
        double sumCredit = tariff.getSum();
        double sumPercent = countSumPercent(tariff);

        // сумма которую должны заплатить к текущему моменту
        double sumLoanByDate = paymentScheduleRepository.getSumLoanByDate(credit, date);
        double sumPercentByDate = paymentScheduleRepository.getSumPercentByDate(credit, date);

        // сумма выплат
        double sumPaymentLoanByDate = paymentHistoryRepository.getSumLoanByDate(credit, date);
        double sumPaymentPercentByDate = paymentHistoryRepository.getSumPercentByDate(credit, date);

        // проверка на просрочку
        double currentDebtLoan = max(0, sumLoanByDate - sumPaymentLoanByDate);
        double currentDebtPercent = max(0, sumPercentByDate - sumPaymentPercentByDate);

        // сколько должны заплатить чтоб погасить просрочку
        double currentPaymentPercent = min(currentDebtPercent, sum);
        double currentPaymentLoan = min(currentDebtLoan, sum - currentPaymentPercent);

        // оставшая сумма чтобы заплатить досрочно
        double remainSum = sum - currentPaymentPercent - currentPaymentLoan;

        currentPaymentLoan += remainSum;

        // сколько останется заплатить после текущего платежа
        double remainLoan = max(0, sumCredit - sumPaymentLoanByDate - currentPaymentLoan);
        double remainPercent = max(0, sumPercent - sumPaymentPercentByDate - currentPaymentPercent);

        if(remainSum == 0 && remainPercent == 0){
            closeCredit(credit);
        }

        if(remainSum > 0){
            Date nearestDate = new Date(paymentScheduleRepository.getNearestDate(credit, date).getTime());

            // сколько месяцев осталось платить
            int remainPeriod = paymentScheduleRepository.getRemainPeriod(credit, nearestDate);

            paymentScheduleRepository.deletePaymentsAfterDate(credit, nearestDate);

            PaymentSchedule paymentSchedule = new PaymentSchedule();
            paymentSchedule.setDate(date);
            paymentSchedule.setCredit(credit);
            paymentSchedule.setSumLoan(remainSum);
            paymentSchedule.setSumPercent(0);
            paymentScheduleRepository.save(paymentSchedule);

            countAnnuity(nearestDate, remainLoan, remainPeriod, tariff.getRate(), credit);

        }

        PaymentHistory payment = new PaymentHistory();
        payment.setPaymentType(paymentType);
        payment.setCredit(credit);
        payment.setDate(date);
        payment.setSumLoan(currentPaymentLoan);
        payment.setSumPercent(currentPaymentPercent);

        paymentHistoryRepository.save(payment);
    }

    private void closeCredit(Credit credit){
        credit.setIsActive(false);
        creditRepository.save(credit);
    }

    // подсчет аннуитетного платежа
    private void countAnnuity(Date date, double sum, int period, int rate, Credit credit){
        double monthRate = (double)rate / (100 * 12);
        double annuityPayment = (int)(sum * (monthRate / (1 - Math.pow(1 + monthRate, -period))));

        double sumMonthLoan = sum / period;
        double sumMonthPercent = annuityPayment - sumMonthLoan;

        for(int i = 0; i < period; ++i){
            Date newDate = Date.valueOf(date.toLocalDate().plusMonths(i));

            PaymentSchedule payment = new PaymentSchedule();
            payment.setCredit(credit);
            payment.setSumLoan(sumMonthLoan);
            payment.setSumPercent(sumMonthPercent);
            payment.setDate(newDate);

            paymentScheduleRepository.save(payment);
        }
    }

    private double countSumPercent(Tariff tariff){
        double sum = tariff.getSum();
        int rate = tariff.getRate();
        int period = tariff.getPeriod();

        double monthRate = (double)rate / (100 * 12);
        double annuityPayment = (int) (sum * (monthRate / (1 - Math.pow(1 + monthRate, -period))));

        return annuityPayment * period - sum;
    }
}
