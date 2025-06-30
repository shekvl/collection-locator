package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"row", "rowAmount"})
public class ApiAnonymizationDtoDataDto {
    private String row;

    private Integer rowAmount;

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Integer getRowAmount() {
        return rowAmount;
    }

    public void setRowAmount(Integer rowAmount) {
        this.rowAmount = rowAmount;
    }
}
