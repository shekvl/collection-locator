package com.indexcreationweb.indexcreationweb.services;

import com.google.gson.Gson;
import com.indexcreationweb.indexcreationweb.commands.DefinitionCommand;
import com.indexcreationweb.indexcreationweb.commands.DefinitionCommandColumnCommand;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDownloadDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDownloadDtoColumnDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDtoColumnDto;
import com.indexcreationweb.indexcreationweb.entities.indexcreator.Definition;
import com.indexcreationweb.indexcreationweb.entities.indexcreator.DefinitionColumn;
import com.indexcreationweb.indexcreationweb.entities.indexcreator.Loinc;
import com.indexcreationweb.indexcreationweb.repositories.indexcreator.DefinitionRepository;
import com.indexcreationweb.indexcreationweb.repositories.indexcreator.LoincRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefinitionService {
    Logger logger = LoggerFactory.getLogger(DefinitionService.class);
    private static final Pattern PATTERN = Pattern.compile("^(.*) \\((\\d+)\\)$");

    @Autowired
    DefinitionRepository definitionRepository;

    @Autowired
    LoincRepository loincRepository;

    public Definition findbyId(Long id) {
        Optional<Definition> byId = definitionRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<Definition> findAll() {
        List<Definition> definitions = new LinkedList<>();
        for (Definition definition : definitionRepository.findAllByOrderByIdAsc()) {
            definitions.add(definition);
        }

        return definitions;
    }

    public Integer findNumberOfDefinitions() {
        return ((java.util.Collection<?>) definitionRepository.findAll()).size();
    }

    public static String[] parseNameAndNumber(String input) {
        Matcher m = PATTERN.matcher(input);
        if (m.matches()) {
            String text = m.group(1);
            String number = m.group(2);
            return new String[]{ text, number };
        } else {
            // no match: you can throw or return something else
            return null;
        }
    }

    public Definition save(DefinitionCommand command) {
        Definition definition = new Definition();
        definition.setName(command.getName());
        definition.setTargetK(command.getTargetK());
        definition.setFast(command.getFast());
        definition.setBatch(command.getBatch());
        Integer cnt = 1;
        List<DefinitionColumn> columnList = new LinkedList<>();
        if (command.getColumns() != null) {
            for (DefinitionCommandColumnCommand column : command.getColumns()) {
                if (column != null && column.getName() != null && column.getCodeText() != null) {
                    DefinitionColumn col = new DefinitionColumn();
                    col.setPosition(cnt);
                    cnt++;
                    col.setName(column.getName());
                    String codeText = column.getCodeText();

                    String[] nameAndNumber = parseNameAndNumber(codeText);
                    col.setCodeText(nameAndNumber[0]);

                    col.setCode(nameAndNumber[1]);
                    columnList.add(col);
                }
            }
        }
        definition.setColumns(new HashSet<>(columnList));

        return definitionRepository.save(definition);
    }

    public Definition edit(DefinitionCommand command, String id) {
        Definition definition = definitionRepository.findById(Long.parseLong(id));
        definition.setName(command.getName());
        definition.setTargetK(command.getTargetK());
        definition.setFast(command.getFast());
        definition.setBatch(command.getBatch());
        Integer cnt = 1;
        List<DefinitionColumn> columnList = new LinkedList<>();
        if (command.getColumns() != null) {
            for (DefinitionCommandColumnCommand column : command.getColumns()) {
                if (column != null && column.getName() != null && column.getCodeText() != null) {
                    DefinitionColumn col = new DefinitionColumn();
                    col.setPosition(cnt);
                    cnt++;
                    col.setName(column.getName());
//                    col.setCode(column.getCode());
//                    col.setCodeText(column.getCodeText());

                    String codeText = column.getCodeText();

                    String[] nameAndNumber = parseNameAndNumber(codeText);
                    col.setCodeText(nameAndNumber[0]);

                    col.setCode(nameAndNumber[1]);
                    columnList.add(col);
                }
            }
        }
        definition.getColumns().clear();
        definition.getColumns().addAll(new HashSet<>(columnList));

        return definitionRepository.save(definition);
    }

    public DefinitionCommand getCommandFromId(String id) {
        Definition definition = definitionRepository.findById(Long.parseLong(id));
        DefinitionCommand command = new DefinitionCommand();
        command.setName(definition.getName());
        command.setTargetK(definition.getTargetK());
        command.setFast(definition.getFast());
        command.setBatch(definition.getBatch());
        List<DefinitionCommandColumnCommand> columnsList = new LinkedList<>();
        for (DefinitionColumn column : definition.getColumns()) {
            DefinitionCommandColumnCommand columnCommand = new DefinitionCommandColumnCommand();
            columnCommand.setName(column.getName());
            columnCommand.setCode(column.getCode());
            columnCommand.setCodeText(column.getCodeText());
            Optional<Loinc> loincOptional = loincRepository.findById(column.getCode());
            if (loincOptional.isPresent()) {
                Loinc loinc = loincOptional.get();
                columnCommand.setCodeText(loinc.getLong_common_name() + " (" + loinc.getLoinc_num() + ")");
            } else {
                columnCommand.setCodeText("Name not found (" + column.getCode() + ")");
            }
            columnsList.add(columnCommand);
        }
        command.setColumns(columnsList);
        return command;
    }

    public List<DefinitionDto> getDtoListFromDefinitionList(List<Definition> definitions) {
        List<DefinitionDto> definitionDtos = new LinkedList<>();
        for (Definition definition : definitions) {
            DefinitionDto definitionDto = getDtoFromDefinition(definition);
            definitionDtos.add(definitionDto);
        }
        return definitionDtos;
    }

    public DefinitionDto getDtoFromDefinition(Definition definition) {
        DefinitionDto dto = new DefinitionDto();
        dto.setId(definition.getId());
        dto.setName(definition.getName());
        dto.setTargetK(definition.getTargetK());
        dto.setFast(definition.getFast());
        dto.setBatch(definition.getBatch());
        List<DefinitionDtoColumnDto> columnsList = new LinkedList<>();
        for (DefinitionColumn column : definition.getColumns()) {
            DefinitionDtoColumnDto columnDto = new DefinitionDtoColumnDto();
            columnDto.setPosition(column.getPosition());
            columnDto.setName(column.getName());
            columnDto.setCode(column.getCode());
            columnDto.setCodeText(column.getCodeText());
            columnsList.add(columnDto);
        }
        Collections.sort(columnsList);
        dto.setColumns(columnsList);
        return dto;
    }

    public InputStreamResource getDownloadDataJsonFromId(String id) {
        DefinitionDownloadDto dto = getDefinitionDownloadDto(id);

        Gson gson = new Gson();
        String json = gson.toJson(dto);
        return new InputStreamResource(new ByteArrayInputStream(json.getBytes()));
    }

    public InputStreamResource getDownloadDataXmlFromId(String id) throws JAXBException {
        DefinitionDownloadDto dto = getDefinitionDownloadDto(id);
        JAXBContext context = JAXBContext.newInstance(DefinitionDownloadDto.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(dto, sw);
        String xmlString = sw.toString();
        return new InputStreamResource(new ByteArrayInputStream(xmlString.getBytes()));
    }

    public DefinitionDownloadDto getDefinitionDownloadDto(String id) {
        Definition definition = definitionRepository.findById(Long.parseLong(id));
        DefinitionDownloadDto dto = new DefinitionDownloadDto();
        dto.setuId(definition.getName() + "_" + definition.getId());
        dto.setName(definition.getName());
        dto.setTargetK(definition.getTargetK());
        dto.setFast(definition.getFast());
        dto.setBatch(definition.getBatch());
        List<DefinitionDownloadDtoColumnDto> columnsList = new LinkedList<>();
        for (DefinitionColumn column : definition.getColumns()) {
            DefinitionDownloadDtoColumnDto columnDownloadDto = new DefinitionDownloadDtoColumnDto();
            columnDownloadDto.setPosition(column.getPosition());
            columnDownloadDto.setName(column.getName());
            columnDownloadDto.setCode(column.getCode());
            columnDownloadDto.setCodeText(column.getCodeText());
            columnsList.add(columnDownloadDto);
        }
        Collections.sort(columnsList);
        dto.setColumns(columnsList);
        return dto;
    }

    public void delete(Long id) {
        definitionRepository.deleteById(id);
    }

    public List<DefinitionDownloadDto> getAllDownloadDataJson() {
        List<Definition> definitions = findAll();
//        List<String> jsonList = new ArrayList<>();
        List<DefinitionDownloadDto> list = new ArrayList<>();
        for (Definition d : definitions) {
            Long id = d.getId();
            DefinitionDownloadDto dto = getDefinitionDownloadDto(String.valueOf(id));

//            Gson gson = new Gson();
//            String json = gson.toJson(dto);
            list.add(dto);

        }
        return list;
    }
}
