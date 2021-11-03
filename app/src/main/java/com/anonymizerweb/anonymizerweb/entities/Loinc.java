package com.anonymizerweb.anonymizerweb.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Loinc {
    @Id
    private String lonc_num;

    private String long_common_name;

    public String getLonc_num() {
        return lonc_num;
    }

    public void setLonc_num(String lonc_num) {
        this.lonc_num = lonc_num;
    }

    public String getLong_common_name() {
        return long_common_name;
    }

    public void setLong_common_name(String long_common_name) {
        this.long_common_name = long_common_name;
    }
}
