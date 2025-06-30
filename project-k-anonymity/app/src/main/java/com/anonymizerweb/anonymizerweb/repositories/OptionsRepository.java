package com.anonymizerweb.anonymizerweb.repositories;

import com.anonymizerweb.anonymizerweb.entities.Options;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OptionsRepository extends CrudRepository<Options, String> {

    Optional<Options> findById(String id);

    @Override
    List<Options> findAll();
}
