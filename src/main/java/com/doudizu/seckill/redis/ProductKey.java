package com.doudizu.seckill.redis;

public class ProductKey extends BasePrefix{

	private ProductKey(String prefix) {
		super(prefix);
	}
	public static ProductKey getByPid = new ProductKey("pid");
}
