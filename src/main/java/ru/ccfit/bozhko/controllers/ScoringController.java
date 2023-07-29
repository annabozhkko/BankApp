package ru.ccfit.bozhko.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.services.ClientService;
import ru.ccfit.bozhko.services.ScoringService;
import ru.ccfit.bozhko.services.TariffService;

@Controller
@RequestMapping("/scoring/{id}")
public class ScoringController {
    private final ScoringService scoringService;
    private final TariffService tariffService;
    private final ClientService clientService;

    @Autowired
    public ScoringController(ScoringService scoringService, TariffService tariffService, ClientService clientService){
        this.scoringService = scoringService;
        this.tariffService = tariffService;
        this.clientService = clientService;
    }

    @GetMapping()
    public String index(@PathVariable("id") int id, Model model, Pageable pageable, HttpServletRequest httpServletRequest) {
        model.addAttribute("page", tariffService.findAll(pageable));
        model.addAttribute("client", clientService.findOne(id));
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "scoring/index";
    }

    @PostMapping()
    public String create(@PathVariable("id") int tariffId, Client client) {
        scoringService.createScoring(tariffService.findOne(tariffId), client);
        return "redirect:/clients/filter";
    }
}
