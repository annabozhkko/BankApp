package ru.ccfit.bozhko.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ccfit.bozhko.models.*;
import ru.ccfit.bozhko.services.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequestMapping("/credits")
public class CreditController {
    private final CreditService creditService;
    private final ScoringService scoringService;
    private final ClientService clientService;
    private final PaymentScheduleService paymentScheduleService;
    private final PaymentHistoryService paymentHistoryService;

    @Autowired
    public CreditController(CreditService creditService, ScoringService scoringService, ClientService clientService,
                            PaymentScheduleService paymentScheduleService, PaymentHistoryService paymentHistoryService) {
        this.creditService = creditService;
        this.scoringService = scoringService;
        this.clientService = clientService;
        this.paymentScheduleService = paymentScheduleService;
        this.paymentHistoryService = paymentHistoryService;
    }

    @GetMapping()
    public String index(Model model, Pageable pageable, HttpServletRequest httpServletRequest,
                        @RequestParam(name = "name", required = false) String clientName,
                        @RequestParam(name = "passport", required = false) String passportDetails,
                        @RequestParam(name = "sum", required = false) Double sum,
                        @RequestParam(name = "percent", required = false) Integer percent,
                        @RequestParam(name = "period", required = false) Integer period,
                        @RequestParam(name = "date", required = false) String dateString,
                        @RequestParam(name = "status", required = false) String status,
                        @RequestParam(name = "profit_from", required = false) Double profitFrom,
                        @RequestParam(name = "profit_to", required = false) Double profitTo){
        Page<Credit> page = creditService.getFilteredCredits(clientName, passportDetails, sum, percent, period,
                (Objects.equals(dateString, "")) ? null : LocalDate.parse(dateString), status, profitFrom, profitTo, pageable);

        model.addAttribute("page", page);
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "credits/index";
    }

    @GetMapping("/filter")
    public String showFilter(){
        return "credits/filter";
    }

    @GetMapping("/{id}/showScoring")
    public String showScoringCredit(@PathVariable("id") int clientId, Model model) {
        Client client = clientService.findOne(clientId);
        model.addAttribute("credits", scoringService.findByClient(client));
        return "credits/showScoring";
    }

    @PostMapping("/{id}/showScoring")
    public String create(Model model, @PathVariable("id") int scoringId, @RequestParam("date")String dateString){
        Scoring scoring = scoringService.findOne(scoringId);

        if(creditService.findActiveCreditsByClient(scoring.getClient()).size() > 0){
            model.addAttribute("errorMessage", "Client has active credits.");
            return "errorMessage";
        }

        creditService.createCredit(scoring, LocalDate.parse(dateString));

        return "redirect:/clients/filter";
    }

    @GetMapping("/{id}/creditHistory")
    public String showCreditHistory(@PathVariable("id") int clientId, Model model) {
        Client client = clientService.findOne(clientId);
        model.addAttribute("credits", creditService.findCreditsByClient(client));
        return "credits/creditHistory";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        Credit credit = creditService.findOne(id);
        paymentHistoryService.deleteAllByCredit(credit);
        paymentScheduleService.deleteAllByCredit(credit);
        creditService.delete(id);

        return "redirect:/credits/filter";
    }

}
