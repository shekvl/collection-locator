package com.anonymizerweb.anonymizerweb.repositories.cdm;

import com.anonymizerweb.anonymizerweb.entities.cdm.CdmConcept;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CdmConceptRepository extends JpaRepository<CdmConcept, String> {
    CdmConcept findCdmConceptByName(String name);
    CdmConcept findCdmConceptById(String id);

    @Query("from CdmConcept c where (UPPER(c.name) LIKE CONCAT('%',UPPER(:queryString),'%')) OR (UPPER(CAST(c.id as string)) LIKE CONCAT('%',UPPER(:queryString),'%')) ORDER BY c.id ASC")
    List<CdmConcept> findForAutocomplete(@Param("queryString") String queryString, Pageable pageable);
}
