package com.indexcreationweb.indexcreationweb.repositories.indexcreator;

import com.indexcreationweb.indexcreationweb.entities.indexcreator.Definition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DefinitionRepository extends CrudRepository<Definition, Long> {

    Definition findById(long id);
    public List<Definition> findAllByOrderByIdAsc();
}
