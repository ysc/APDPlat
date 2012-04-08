package com.apdplat.platform.criteria;
/**
 * 条件符号定义
 * @author 杨尚川
 *
 */
public enum Criteria {
	and("and"),or("or");
	private Criteria(String symbol){
		this.symbol=symbol;
	}
	
	private String symbol;
	
	public String getSymbol() {
		return symbol;
	}
}
