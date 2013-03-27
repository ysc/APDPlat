/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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