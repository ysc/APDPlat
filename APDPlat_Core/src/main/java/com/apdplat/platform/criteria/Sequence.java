package com.apdplat.platform.criteria;

/**
 *排序条件的顺序，升序和降序
 * @author 杨尚川
 */
public enum Sequence {
    DESC("desc"),ASC("asc");
    private Sequence(String value){
        this.value=value;
    }
    private String value;
    public String getValue(){
        return this.value;
    }
}
