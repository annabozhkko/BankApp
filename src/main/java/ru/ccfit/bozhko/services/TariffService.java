package ru.ccfit.bozhko.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Tariff;
import ru.ccfit.bozhko.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TariffService {
    private final TariffRepository repository;

    @Autowired
    public TariffService(TariffRepository tariffRepository) {
        this.repository = tariffRepository;
    }

    public Page<Tariff> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Tariff findOne(int id) {
        Optional<Tariff> foundClient = repository.findById(id);
        return foundClient.orElse(null);
    }

    @Transactional
    public void save(Tariff tariff) {
        repository.save(tariff);
    }

    @Transactional
    public void update(int id, Tariff updatedTariff) {
        updatedTariff.setId(id);
        repository.save(updatedTariff);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}
