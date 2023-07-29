package ru.ccfit.bozhko.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.repository.BlockedClientRepository;
import ru.ccfit.bozhko.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.repository.CreditRepository;
import ru.ccfit.bozhko.repository.ScoringRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepository clientRepository;
    private final ScoringRepository scoringRepository;
    private final CreditRepository creditRepository;
    private final BlockedClientRepository blockedClientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, ScoringRepository scoringRepository, CreditRepository creditRepository, BlockedClientRepository blockedClientRepository) {
        this.clientRepository = clientRepository;
        this.scoringRepository = scoringRepository;
        this.creditRepository = creditRepository;
        this.blockedClientRepository = blockedClientRepository;
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findOne(int id) {
        Optional<Client> foundClient = clientRepository.findById(id);
        return foundClient.orElse(null);
    }

    @Transactional
    public void save(Client client) {
        clientRepository.save(client);
    }

    @Transactional
    public void update(int id, Client updatedClient) {
        updatedClient.setId(id);
        clientRepository.save(updatedClient);
    }

    @Transactional
    public void delete(int id) {
        clientRepository.deleteById(id);
    }

    public Page<Client> getFilteredClients(String name, String passportDetails, String status, Pageable pageable){
        List<Client> filteredClients = findAll();

        filteredClients = filteredClients.stream().filter(client -> Objects.equals(name, "") || Objects.equals(client.getFullname(), name))
                .filter(client -> Objects.equals(passportDetails, "") || Objects.equals(client.getPassportDetails(), passportDetails)).collect(Collectors.toList());

        if(Objects.equals(status, "No credits / tariff available")){
            filteredClients = filteredClients.stream().filter(client ->
                    !scoringRepository.findByClient(client).isEmpty() &&
                            creditRepository.findActiveCreditsByClient(client).isEmpty()).collect(Collectors.toList());
        }

        if(Objects.equals(status, "Credit available")){
            filteredClients = filteredClients.stream().filter(client ->
                    !creditRepository.findActiveCreditsByClient(client).isEmpty()).collect(Collectors.toList());
        }

        if(Objects.equals(status, "No tariff")){
            filteredClients = filteredClients.stream().filter(client ->
                    scoringRepository.findByClient(client).isEmpty() &&
                            creditRepository.findActiveCreditsByClient(client).isEmpty()).collect(Collectors.toList());
        }

        if(Objects.equals(status, "Blocked")){
            filteredClients = filteredClients.stream().filter(client ->
                    !blockedClientRepository.findByClient(client, Date.valueOf(LocalDate.now())).isEmpty()).collect(Collectors.toList());
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Client> page = new PageImpl<>(filteredClients, pageRequest, filteredClients.size());

        return page;
    }
}