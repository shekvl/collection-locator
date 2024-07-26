package com.indexcreationweb.indexcreationweb.repositories.indexcreator;

import com.indexcreationweb.indexcreationweb.entities.indexcreator.Definition;
import org.springframework.data.repository.CrudRepository;

public interface DefinitionRepository extends CrudRepository<Definition, Long> {

    Definition findById(long id);
}
