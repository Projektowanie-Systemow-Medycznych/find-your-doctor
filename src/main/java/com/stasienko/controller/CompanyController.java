package com.stasienko.controller;

import com.stasienko.model.Company;
import com.stasienko.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String getAllCompanies(Model model) {
        List<Company> companies = companyService.getAllCompanies();
        model.addAttribute("companies", companies);
        return "companies/list";
    }

    @GetMapping("/new")
    public String showAddCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        return "companies/add";
    }

    @PostMapping("/new")
    public String addCompany(@ModelAttribute("company") Company company) {
        companyService.addCompany(company);
        return "redirect:/companies";
    }

    @GetMapping("/edit/{id}")
    public String showEditCompanyForm(@PathVariable("id") UUID id, Model model) {
        Company company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "companies/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCompany(@PathVariable("id") UUID id, @ModelAttribute("company") Company company) {
        companyService.updateCompany(id, company);
        return "redirect:/companies";
    }

    @GetMapping("/delete/{id}")
    public String deleteCompany(@PathVariable("id") UUID id) {
        companyService.deleteCompany(id);
        return "redirect:/companies";
    }
}
