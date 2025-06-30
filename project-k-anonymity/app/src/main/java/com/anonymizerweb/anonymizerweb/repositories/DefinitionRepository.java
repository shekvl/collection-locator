package com.anonymizerweb.anonymizerweb.repositories;

import com.anonymizerweb.anonymizerweb.entities.Definition;
import org.springframework.data.repository.CrudRepository;

public interface DefinitionRepository extends CrudRepository<Definition, Long> {

    Definition findById(long id);
}
