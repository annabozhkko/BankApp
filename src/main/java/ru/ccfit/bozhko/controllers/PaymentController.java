package ru.ccfit.bozhko.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ccfit.bozhko.models.*;
import ru.ccfit.bozhko.services.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentScheduleService paymentScheduleService;
    private final PaymentHistoryService paymentHistoryService;
    private final CreditService creditService;
    private final PaymentTypeService paymentTypeService;
    private final ClientService clientService;

    @Autowired
    public PaymentController(PaymentScheduleService paymentScheduleService, PaymentHistoryService paymentHistoryService,
                             CreditService creditService, PaymentTypeService paymentTypeService, ClientService clientService){
        this.paymentScheduleService = paymentScheduleService;
        this.paymentHistoryService = paymentHistoryService;
        this.creditService = creditService;
        this.paymentTypeService = paymentTypeService;
        this.clientService = clientService;
    }

    @GetMapping()
    public String index(Model model, Pageable pageable, HttpServletRequest httpServletRequest){
        model.addAttribute("page", creditService.findActiveCredits(pageable));
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "payments/index";
    }

    @GetMapping("/scheduleClient/{id}")
    public String showScheduleClient(@PathVariable("id") int clientId, Model model){
        Client client = clientService.findOne(clientId);
        List<Credit> activeCredits = creditService.findActiveCreditsByClient(client);
        List<PaymentSchedule> payments;
        if(activeCredits.size() == 1){
            payments = paymentScheduleService.getCreditPayments(activeCredits.get(0));
        }
        else {
            payments = new ArrayList<>();
        }
        model.addAttribute("payments", payments);
        return "payments/scheduleClient";
    }

    @GetMapping("/new/{id}")
    public String newPayment(@PathVariable("id") int creditId, Model model){
        model.addAttribute("payment_types", paymentTypeService.findAll());
        model.addAttribute("credit", creditService.findOne(creditId));
        return "payments/new";
    }

    @PostMapping("/new/{id}")
    public String createPayment(@PathVariable("id") int creditId, @RequestParam("date") String dateString,
                                @RequestParam("sum") int sum, @RequestParam("payment_type") PaymentType paymentType){
        Credit credit = creditService.findOne(creditId);
        paymentHistoryService.createPayment(credit, Date.valueOf(LocalDate.parse(dateString)), sum, paymentType);

        return "redirect:/";
    }

    @GetMapping("/delay/{id}")
    public String showDelayClient(@PathVariable("id") int clientId, Model model){
        Client client = clientService.findOne(clientId);
        List<Credit> activeCredits = creditService.findActiveCreditsByClient(client);
        if(activeCredits.size() == 0){
            model.addAttribute("errorMessage", "No active credit found");
            return "errorMessage";
        }

        Credit credit = activeCredits.get(0);

        model.addAttribute("credit", credit);
        model.addAttribute("delay_loan", paymentScheduleService.getDelayLoan(credit));
        model.addAttribute("delay_percent", paymentScheduleService.getDelayPercent(credit));

        return "payments/delay";
    }

}
