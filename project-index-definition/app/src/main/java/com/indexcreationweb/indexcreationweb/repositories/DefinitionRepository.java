package com.indexcreationweb.indexcreationweb.repositories;

import com.indexcreationweb.indexcreationweb.entities.Definition;
import org.springframework.data.repository.CrudRepository;

public interface DefinitionRepository extends CrudRepository<Definition, Long> {

    Definition findById(long id);
}
