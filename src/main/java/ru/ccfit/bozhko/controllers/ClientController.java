package ru.ccfit.bozhko.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Client;
import ru.ccfit.bozhko.services.BlockedClientService;
import ru.ccfit.bozhko.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final BlockedClientService blockedClientService;

    @Autowired
    public ClientController(ClientService clientService, BlockedClientService blockedClientService) {
        this.clientService = clientService;
        this.blockedClientService = blockedClientService;
    }

    @GetMapping()
    public String index(Model model, Pageable pageable, HttpServletRequest httpServletRequest,
                        @RequestParam(name = "name", required = false) String name,
                        @RequestParam(name = "passport", required = false) String passportDetails,
                        @RequestParam(name = "status", required = false) String status) {
        Page<Client> page = clientService.getFilteredClients(name, passportDetails, status, pageable);

        model.addAttribute("page", page);
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "clients/index";
    }

    @GetMapping("/filter")
    public String showFilter(){
        return "clients/filter";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("client", clientService.findOne(id));
        return "clients/show";
    }

    @GetMapping("/new")
    public String newClient(@ModelAttribute("client") Client client) {
        return "clients/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("client") Client client, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "clients/new";

        try {
            clientService.save(client);
        }catch (Exception e){
            model.addAttribute("errorMessage", "Data entered incorrectly");
            return "errorMessage";
        }

        return "redirect:/clients/filter";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("client", clientService.findOne(id));
        return "clients/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("client") Client client, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (bindingResult.hasErrors())
            return "clients/edit";

        try {
            clientService.update(id, client);
        }catch (Exception e){
            model.addAttribute("errorMessage", "Data entered incorrectly");
            return "errorMessage";
        }
        return "redirect:/clients/filter";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        clientService.delete(id);
        return "redirect:/clients/filter";
    }

    @GetMapping("/{id}/block")
    public String blockClientForm(@PathVariable("id") int id, Model model) {
        model.addAttribute("client", clientService.findOne(id));
        return "clients/block";
    }

    @PostMapping("/{id}/block")
    public String blockClient(@ModelAttribute("client") Client client,
                              @RequestParam("date1")String dateString1, @RequestParam("date2")String dateString2) {
        blockedClientService.blockClient(client, LocalDate.parse(dateString1), LocalDate.parse(dateString2));
        return "redirect:/clients/{id}";
    }
}
