package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.models.Scoring;
import ru.ccfit.bozhko.models.Tariff;
import ru.ccfit.bozhko.repository.ScoringRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScoringService {
    private final ScoringRepository repository;

    @Autowired
    public ScoringService(ScoringRepository repository) {
        this.repository = repository;
    }

    public Page<Scoring> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Scoring findOne(int id) {
        Optional<Scoring> foundPayment = repository.findById(id);
        return foundPayment.orElse(null);
    }

    @Transactional
    public void save(Scoring scoring) {
        repository.save(scoring);
    }

    @Transactional
    public void update(int id, Scoring updatedScoring) {
        updatedScoring.setId(id);
        repository.save(updatedScoring);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<Scoring> findByClient(Client client){
        return repository.findByClient(client);
    }

    public void createScoring(Tariff tariff, Client client){
        Scoring scoring = new Scoring();
        scoring.setClient(client);
        scoring.setTariff(tariff);
        scoring.setApprovalStatus(false);
        repository.save(scoring);
    }
}
