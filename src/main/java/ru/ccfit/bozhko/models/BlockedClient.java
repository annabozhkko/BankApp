package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "blocked_clients")
public class BlockedClient{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "dt1")
    private Date date1;

    @Column(name = "dt2")
    private Date date2;
}