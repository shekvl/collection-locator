package com.indexcreationweb.indexcreationweb.services;

import com.indexcreationweb.indexcreationweb.dto.CdmConceptAutocompleteDto;
import com.indexcreationweb.indexcreationweb.entities.cdm.CdmConcept;
import com.indexcreationweb.indexcreationweb.repositories.cdm.CdmConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CdmConceptService {
    Logger logger = LoggerFactory.getLogger(CdmConceptService.class);

    @Autowired
    CdmConceptRepository CdmConceptRepository;

    public List<CdmConceptAutocompleteDto> findForAutocomplete(String query) {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "id");
        if(query == null || query.isEmpty()){
            query="0";
        }
        return CdmConceptEntityToAutocompleteDto(CdmConceptRepository.findForAutocomplete(query, pageable));
    }

    private List<CdmConceptAutocompleteDto> CdmConceptEntityToAutocompleteDto(List<CdmConcept> concepts) {
        List<CdmConceptAutocompleteDto> result = new LinkedList<>();
        for (CdmConcept concept : concepts) {
            CdmConceptAutocompleteDto cdmConceptAutocompleteDto = new CdmConceptAutocompleteDto();
            cdmConceptAutocompleteDto.setId(concept.getId());
            cdmConceptAutocompleteDto.setSlug(concept.getName());
            cdmConceptAutocompleteDto.setText(concept.getName() + " (" + concept.getId() + ")");
            result.add(cdmConceptAutocompleteDto);
        }

        return result;
    }
}
