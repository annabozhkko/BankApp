package ru.ccfit.bozhko.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/query")
public class QueryController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QueryController(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/result")
    public String executeSqlQuery(Model model, @RequestParam("query") String sqlQuery) {
        if(Objects.equals(sqlQuery, "")){
            return "query/input";
        }
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
            if(!result.isEmpty()){
                Set<String> columnNames = result.get(0).keySet();
                model.addAttribute("columnNames", columnNames);
            }
            model.addAttribute("results", result);
        }catch (BadSqlGrammarException ex){
            model.addAttribute("errorMessage", ex.getMessage());
            return "errorMessage";
        }

        return "query/result";
    }

    @GetMapping("/input")
    public String inputSqlQuery(){
        return "query/input";
    }

}
