package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.*;
import ru.ccfit.bozhko.repository.CreditRepository;
import ru.ccfit.bozhko.repository.PaymentHistoryRepository;
import ru.ccfit.bozhko.repository.PaymentScheduleRepository;
import ru.ccfit.bozhko.repository.ScoringRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CreditService {
    private final CreditRepository creditRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ScoringRepository scoringRepository;

    @Autowired
    public CreditService(CreditRepository creditRepository, PaymentScheduleRepository paymentScheduleRepository, PaymentHistoryRepository paymentHistoryRepository, ScoringRepository scoringRepository) {
        this.creditRepository = creditRepository;
        this.paymentScheduleRepository = paymentScheduleRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.scoringRepository = scoringRepository;
    }

    public Page<Credit> findAll(Pageable pageable) {
        return creditRepository.findAll(pageable);
    }

    public List<Credit> findAll() {
        return creditRepository.findAll();
    }

    public Credit findOne(int id) {
        Optional<Credit> foundCredit = creditRepository.findById(id);
        return foundCredit.orElse(null);
    }

    @Transactional
    public void save(Credit credit) {
        creditRepository.save(credit);
    }

    @Transactional
    public void update(int id, Credit updatedCredit) {
        updatedCredit.setId(id);
        creditRepository.save(updatedCredit);
    }

    @Transactional
    public void delete(int id) {
        creditRepository.deleteById(id);
    }

    @Transactional
    public List<Credit> findActiveCreditsByClient(Client client){
        return creditRepository.findActiveCreditsByClient(client);
    }

    @Transactional
    public Page<Credit> findActiveCredits(Pageable pageable){
        return creditRepository.findActiveCredits(pageable);
    }

    @Transactional
    public List<Credit> findCreditsByClient(Client client){
        return creditRepository.findCreditsByClient(client);
    }

    public Page<Credit> getFilteredCredits(String clientName, String passportDetails, Double sum, Integer percent, Integer period,
                                           LocalDate date, String status, Double profitFrom, Double profitTo,
                                           Pageable pageable){
        List<Credit> filteredCredits = findAll();

        filteredCredits = filteredCredits.stream().filter(credit -> Objects.equals(clientName, "") || Objects.equals(credit.getClient().getFullname(), clientName))
                .filter(credit -> Objects.equals(passportDetails, "") || Objects.equals(credit.getClient().getPassportDetails(), passportDetails))
                .filter(credit -> sum == null || credit.getTariff().getSum() == sum)
                .filter(credit -> percent == null || credit.getTariff().getRate() == percent)
                .filter(credit -> period == null || credit.getTariff().getPeriod() == period)
                .filter(credit -> date == null || credit.getDate().compareTo(Date.valueOf(date)) == 0).collect(Collectors.toList());

        if(Objects.equals(status, "Active")){
            filteredCredits = filteredCredits.stream().filter(Credit::getIsActive).collect(Collectors.toList());
        }
        if(Objects.equals(status, "Closed")){
            filteredCredits = filteredCredits.stream().filter(credit -> !credit.getIsActive()).collect(Collectors.toList());
        }
        if(Objects.equals(status, "Overdue")){
            filteredCredits = filteredCredits.stream().filter(credit ->
                    paymentScheduleRepository.getSumLoanByDate(credit, Date.valueOf(LocalDate.now()))
                            + paymentScheduleRepository.getSumPercentByDate(credit, Date.valueOf(LocalDate.now())) >
                            paymentHistoryRepository.getSumLoanByDate(credit, Date.valueOf(LocalDate.now()))
                                    + paymentHistoryRepository.getSumPercentByDate(credit, Date.valueOf(LocalDate.now()))).collect(Collectors.toList());
        }

        if(profitFrom != null){
            filteredCredits = filteredCredits.stream().filter(credit ->
                    (paymentHistoryRepository.getSumLoanByDate(credit, Date.valueOf(LocalDate.now())) +
                            paymentHistoryRepository.getSumPercentByDate(credit, Date.valueOf(LocalDate.now()))) /
                            credit.getTariff().getSum() >= profitFrom).collect(Collectors.toList());

        }

        if(profitTo != null){
            filteredCredits = filteredCredits.stream().filter(credit ->
                    (paymentHistoryRepository.getSumLoanByDate(credit, Date.valueOf(LocalDate.now())) +
                            paymentHistoryRepository.getSumPercentByDate(credit, Date.valueOf(LocalDate.now()))) /
                            credit.getTariff().getSum() <= profitTo).collect(Collectors.toList());

        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Credit> page = new PageImpl<>(filteredCredits, pageRequest, filteredCredits.size());

        return page;
    }

    public void createCredit(Scoring scoring, LocalDate date){
        scoring.setApprovalStatus(true);
        scoringRepository.save(scoring);

        Tariff tariff = scoring.getTariff();

        Date maturityDate = Date.valueOf(date.plusMonths(tariff.getPeriod()));

        Credit credit =  new Credit();
        credit.setClient(scoring.getClient());
        credit.setTariff(tariff);
        credit.setDate(Date.valueOf(date));
        credit.setIsActive(true);
        credit.setMaturityDate(maturityDate);
        creditRepository.save(credit);

        countAnnuity(date, tariff, credit);
    }

    private void countAnnuity(LocalDate date, Tariff tariff, Credit credit){
        double sum = tariff.getSum();
        int rate = tariff.getRate();
        int period = tariff.getPeriod();

        double monthRate = (double)rate / (100 * 12);
        double annuityPayment = sum * (monthRate / (1 - Math.pow(1 + monthRate, -period)));

        double sumMonthLoan = sum / period;
        double sumMonthPercent = annuityPayment - sumMonthLoan;

        for(int i = 1; i <= period; ++i){
            Date newDate = Date.valueOf(date.plusMonths(i));

            PaymentSchedule payment = new PaymentSchedule();
            payment.setCredit(credit);
            payment.setSumLoan(sumMonthLoan);
            payment.setSumPercent(sumMonthPercent);
            payment.setDate(newDate);

            paymentScheduleRepository.save(payment);
        }
    }
}