package ru.ccfit.bozhko.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.models.Credit;
import ru.ccfit.bozhko.models.PaymentHistory;
import ru.ccfit.bozhko.services.ClientService;
import ru.ccfit.bozhko.services.CreditService;
import ru.ccfit.bozhko.services.PaymentHistoryService;
import ru.ccfit.bozhko.services.PaymentTypeService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/paymentHistory")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;
    private final PaymentTypeService paymentTypeService;
    private final ClientService clientService;
    private final CreditService creditService;

    @Autowired
    public PaymentHistoryController(PaymentHistoryService paymentHistoryService, ClientService clientService,
                                    CreditService creditService, PaymentTypeService paymentTypeService){
        this.paymentHistoryService = paymentHistoryService;
        this.clientService = clientService;
        this.creditService = creditService;
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/{id}")
    public String index(@PathVariable("id") int clientId, Model model, Pageable pageable, HttpServletRequest httpServletRequest) {
        Client client = clientService.findOne(clientId);
        List< Credit > activeCredits = creditService.findCreditsByClient(client);
        if(activeCredits.size() == 0){
            model.addAttribute("errorMessage", "No credit found");
            return "errorMessage";
        }

        Page<PaymentHistory> payments = paymentHistoryService.findAllByCredit(activeCredits.get(0), pageable);

        model.addAttribute("page", payments);
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "paymentHistory/index";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("payment", paymentHistoryService.findOne(id));
        return "paymentHistory/show";
    }

    @GetMapping("/new")
    public String newPayment(@ModelAttribute("payment") PaymentHistory paymentHistory, Model model) {
        model.addAttribute("payment_types", paymentTypeService.findAll());
        return "paymentHistory/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("payment") PaymentHistory paymentHistory, @RequestParam("date") LocalDate date,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "paymentHistory/new";
        }

        paymentHistory.setDate(Date.valueOf(date));

        paymentHistoryService.save(paymentHistory);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        PaymentHistory payment = paymentHistoryService.findOne(id);
        model.addAttribute("payment_types", paymentTypeService.findAll());
        model.addAttribute("payment", payment);
        return "paymentHistory/edit";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        paymentHistoryService.delete(id);
        return "redirect:/clients/filter";
    }
}
