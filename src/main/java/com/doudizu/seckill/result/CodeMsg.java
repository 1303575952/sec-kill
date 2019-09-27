package com.doudizu.seckill.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CodeMsg {
	
	private int code;
	private String msg;
	
	//通用的错误码，这里暂未使用
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");

	private CodeMsg( ) {
	}
			
	private CodeMsg( int code,String msg ) {
		this.code = code;
		this.msg = msg;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

}
