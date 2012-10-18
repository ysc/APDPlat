package com.apdplat.platform.criteria;

import java.util.LinkedList;
/**
 * 包含多个排序条件
 * @author 杨尚川
 */
public class OrderCriteria {
	private LinkedList<Order> orders=new LinkedList<>();

	public LinkedList<Order> getOrders() {
		return orders;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}
}
