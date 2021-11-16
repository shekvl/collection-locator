package com.indexcreationweb.indexcreationweb.repositories;

import com.indexcreationweb.indexcreationweb.entities.Loinc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface LoincRepository extends CrudRepository<Loinc, String> {

    Optional<Loinc> findById(String id);

    @Override
    List<Loinc> findAll();

    @Query("from Loinc l where (UPPER(l.long_common_name) LIKE CONCAT('%',UPPER(:queryString),'%')) OR (UPPER(l.loinc_num) LIKE CONCAT('%',UPPER(:queryString),'%')) ORDER BY l.loinc_num ASC")
    List<Loinc> findForAutocomplete(@Param("queryString") String queryString, Pageable pageable);

}
