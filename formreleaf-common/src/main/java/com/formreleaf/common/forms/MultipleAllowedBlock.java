package com.formreleaf.common.forms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/1/15.
 */
//block that allows multiple block
public class MultipleAllowedBlock extends Block {
    private boolean allowMultiple = false;
    private Set<List<Field>> multiFields = new HashSet<>();

    public MultipleAllowedBlock(int id, String title) {
        super(id, title);
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public Set<List<Field>> getMultiFields() {
        return multiFields;
    }

    public void setMultiFields(Set<List<Field>> multiFields) {
        this.multiFields = multiFields;
    }
}
