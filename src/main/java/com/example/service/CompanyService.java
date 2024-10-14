package com.example.service;

import com.example.model.Company;
import com.example.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    public Company getCompanyById(UUID id) {
        return companyRepository.findById(id).orElse(null);
    }
    public Company addCompany(Company company) {
        if (company.getId() == null) {
            company.setId(UUID.randomUUID());
        }
        return companyRepository.save(company);
    }
    public Company updateCompany(UUID id, Company updatedCompany) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(updatedCompany.getName());
                    company.setDescription(updatedCompany.getDescription());
                    return companyRepository.save(company);
                }).orElseGet(() -> {
                    updatedCompany.setId(id);
                    return companyRepository.save(updatedCompany);
                });
    }
    public void deleteCompany(UUID id) {
        companyRepository.deleteById(id);
    }
}
