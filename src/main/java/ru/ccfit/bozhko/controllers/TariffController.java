package ru.ccfit.bozhko.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.ccfit.bozhko.models.Tariff;
import ru.ccfit.bozhko.services.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tariffs")
public class TariffController {
    private final TariffService tariffService;

    @Autowired
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping()
    public String index(Model model, Pageable pageable, HttpServletRequest httpServletRequest) {
        model.addAttribute("page", tariffService.findAll(pageable));
        model.addAttribute("httpServletRequest", httpServletRequest);
        return "tariffs/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("tariff", tariffService.findOne(id));
        return "tariffs/show";
    }

    @GetMapping("/new")
    public String newTariff(@ModelAttribute("tariff") Tariff tariff) {
        return "tariffs/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("tariff") Tariff tariff, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "tariffs/new";

        tariffService.save(tariff);
        return "redirect:/tariffs";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("tariff", tariffService.findOne(id));
        return "tariffs/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("tariff") Tariff tariff, BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "tariffs/edit";

        tariffService.update(id, tariff);
        return "redirect:/tariffs";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        tariffService.delete(id);
        return "redirect:/tariffs";
    }
}
