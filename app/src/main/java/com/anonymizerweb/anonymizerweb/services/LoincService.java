package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.dto.LoincAutocompleteDto;
import com.anonymizerweb.anonymizerweb.entities.Loinc;
import com.anonymizerweb.anonymizerweb.repositories.LoincRepository;
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
public class LoincService {
    Logger logger = LoggerFactory.getLogger(LoincService.class);

    @Autowired
    LoincRepository loincRepository;

    public List<LoincAutocompleteDto> findForAutocomplete(String query) {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "loinc_num");
        return loincEntityToAuttocompleteDto(loincRepository.findForAutocomplete(query, pageable));
    }

    private List<LoincAutocompleteDto> loincEntityToAuttocompleteDto(List<Loinc> loincs) {
        List<LoincAutocompleteDto> result = new LinkedList<>();
        for (Loinc loinc : loincs) {
            LoincAutocompleteDto loincAutocompleteDto = new LoincAutocompleteDto();
            loincAutocompleteDto.setId(loinc.getLoinc_num());
            loincAutocompleteDto.setSlug(loinc.getLong_common_name());
            loincAutocompleteDto.setText(loinc.getLong_common_name() + " (" + loinc.getLoinc_num() + ")");
            result.add(loincAutocompleteDto);
        }

        return result;
    }
}
