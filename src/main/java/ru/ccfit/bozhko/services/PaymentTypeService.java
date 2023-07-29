package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.PaymentType;
import ru.ccfit.bozhko.repository.PaymentTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PaymentTypeService {
    private final PaymentTypeRepository repository;

    @Autowired
    public PaymentTypeService(PaymentTypeRepository repository) {
        this.repository = repository;
    }

    public List<PaymentType> findAll() {
        return repository.findAll();
    }

    public PaymentType findOne(int id) {
        Optional<PaymentType> foundPayment = repository.findById(id);
        return foundPayment.orElse(null);
    }

    @Transactional
    public void save(PaymentType payment) {
        repository.save(payment);
    }

    @Transactional
    public void update(int id, PaymentType updatedType) {
        updatedType.setId(id);
        repository.save(updatedType);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}
