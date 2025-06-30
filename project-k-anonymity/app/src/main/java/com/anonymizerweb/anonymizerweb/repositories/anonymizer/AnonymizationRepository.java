package com.anonymizerweb.anonymizerweb.repositories.anonymizer;

import com.anonymizerweb.anonymizerweb.entities.anonymizer.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Collection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnonymizationRepository extends CrudRepository<Anonymization, Long> {

    Anonymization findById(long id);
    public List<Anonymization> findAllByOrderByIdAsc();
}
