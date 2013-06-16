/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.platform.model;

/**
 *
 * @author 杨尚川
 */
public class ModelFieldData {
    private String english;
    private String chinese;
    private String type;
    private String simpleDic;
    private String treeDic;
    private boolean manyToOne;
    private String manyToOneRef;

    public boolean isManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(boolean manyToOne) {
        this.manyToOne = manyToOne;
    }

    public String getManyToOneRef() {
        return manyToOneRef;
    }

    public void setManyToOneRef(String manyToOneRef) {
        this.manyToOneRef = manyToOneRef;
    }

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