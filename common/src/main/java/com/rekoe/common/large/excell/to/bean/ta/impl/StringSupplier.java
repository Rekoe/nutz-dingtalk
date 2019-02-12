package com.rekoe.common.large.excell.to.bean.ta.impl;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
class StringSupplier implements Supplier {
    private final String val;

    StringSupplier(String val) {
        this.val = val;
    }

    @Override
    public Object getContent() {
        return val;
    }
}
