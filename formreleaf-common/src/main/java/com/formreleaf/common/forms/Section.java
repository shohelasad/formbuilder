package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Section {
    private Integer id;
    private List<Block> blocks = new ArrayList<>();
    private String instruction;
    private String title;

    public Section(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Section() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
