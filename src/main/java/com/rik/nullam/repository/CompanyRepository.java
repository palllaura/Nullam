package com.rik.nullam.repository;

import com.rik.nullam.entity.participant.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {
    /**
     * Check if a company with the same registry code already exists.
     * @param registryCode Registry code to check.
     * @return true if exists.
     */
    boolean existsCompanyByRegistryCode(String registryCode);
}
