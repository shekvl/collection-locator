package com.indexcreationweb.indexcreationweb.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Loinc {
    @Id
    private String loinc_num;

    private String long_common_name;

    public String getLoinc_num() {
        return loinc_num;
    }

    public void setLoinc_num(String loinc_num) {
        this.loinc_num = loinc_num;
    }

    public String getLong_common_name() {
        return long_common_name;
    }

    public void setLong_common_name(String long_common_name) {
        this.long_common_name = long_common_name;
    }
}
