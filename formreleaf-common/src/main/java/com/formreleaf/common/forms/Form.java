package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Form {
    private Integer id;
    private List<Section> sections = new ArrayList<>();
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
