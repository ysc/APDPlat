package com.apdplat.platform.model;

/**
 *
 * @author ysc
 */
public class ModelFieldData {
    private String english;
    private String chinese;
    private String type;
    private String simpleDic;
    private String treeDic;

    public String getSimpleDic() {
        return simpleDic;
    }

    public void setSimpleDic(String simpleDic) {
        this.simpleDic = simpleDic;
    }

    public String getTreeDic() {
        return treeDic;
    }

    public void setTreeDic(String treeDic) {
        this.treeDic = treeDic;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }    
}
