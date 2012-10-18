package com.apdplat.module.info.model;

/**
 *
 * @author ysc
 */
public enum Lang {
    zh("zh"),en("en");

    private Lang(String symbol) {
        this.symbol = symbol;
    }
    private String symbol;

    public String getSymbol() {
        return symbol;
    }
}
