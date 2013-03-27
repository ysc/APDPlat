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

package com.apdplat.platform.criteria;
/**
 * 排序条件
 * @author 杨尚川
 */
public class Order {
	private String propertyName;
	private Sequence sequence;

	public Order() {
		super();
	}
	public Order(String propertyName, String sequence) {
		super();
		this.propertyName = propertyName;
		this.sequence = Enum.valueOf(Sequence.class, sequence);
	}
	public Order(String propertyName, Sequence sequence) {
		super();
		this.propertyName = propertyName;
		this.sequence = sequence;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public Sequence getSequence() {
		return sequence;
	}
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}
}