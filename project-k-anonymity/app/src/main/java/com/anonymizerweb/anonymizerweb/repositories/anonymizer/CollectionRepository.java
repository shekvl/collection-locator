package com.anonymizerweb.anonymizerweb.repositories.anonymizer;

import com.anonymizerweb.anonymizerweb.entities.anonymizer.Collection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollectionRepository extends CrudRepository<Collection, Long> {

    Collection findById(long id);
    public List<Collection> findAllByOrderByIdAsc();
}
