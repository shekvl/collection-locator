package com.anonymizerweb.anonymizerweb.repositories.anonymizer;

import com.anonymizerweb.anonymizerweb.entities.anonymizer.Definition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DefinitionRepository extends CrudRepository<Definition, Long> {

    Definition findById(long id);
    public List<Definition> findAllByOrderByIdAsc();
}
