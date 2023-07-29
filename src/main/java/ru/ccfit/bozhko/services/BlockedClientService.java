package ru.ccfit.bozhko.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ccfit.bozhko.models.BlockedClient;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.repository.BlockedClientRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BlockedClientService {
    private final BlockedClientRepository repository;

    @Autowired
    public BlockedClientService(BlockedClientRepository repository) {
        this.repository = repository;
    }

    public List<BlockedClient> findAll() {
        return repository.findAll();
    }

    public BlockedClient findOne(int id) {
        Optional<BlockedClient> foundClient = repository.findById(id);
        return foundClient.orElse(null);
    }

    @Transactional
    public void save(BlockedClient client) {
        repository.save(client);
    }

    @Transactional
    public void update(int id, BlockedClient updatedClient) {
        updatedClient.setId(id);
        repository.save(updatedClient);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<BlockedClient> findByClient(Client client, Date date){
        return repository.findByClient(client, date);
    }

    public void blockClient(Client client, LocalDate date1, LocalDate date2){
        BlockedClient blockedClient = new BlockedClient();
        blockedClient.setClient(client);
        blockedClient.setDate1(java.sql.Date.valueOf(date1));
        blockedClient.setDate2(java.sql.Date.valueOf(date2));
        save(blockedClient);
    }
}