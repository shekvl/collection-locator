package com.anonymizerweb.anonymizerweb.repositories;

import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import org.springframework.data.repository.CrudRepository;

public interface AnonymizationRepository extends CrudRepository<Anonymization, Long> {

    Anonymization findById(long id);
}
