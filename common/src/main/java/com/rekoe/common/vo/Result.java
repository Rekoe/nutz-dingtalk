package com.rekoe.common.vo;

import java.util.Iterator;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

public class Result {
	/**
	 * 创建一个异常结果
	 * 
	 * @return 一个异常结果实例,不携带异常信息
	 */
	public static Result exception() {
		return Result.me().setOperationState(OperationState.EXCEPTION);
	}

	/**
	 * 创建一个异常结果
	 * 
	 * @param e
	 *            异常
	 * @return 一个异常结果实例,包含参数异常的信息
	 */
	public static Result exception(Exception e) {
		return Result.exception(e.getMessage());
	}

	/**
	 * 创建一个异常结果
	 * 
	 * @param msg
	 *            异常信息
	 * @return 一个异常结果实例,不携带异常信息
	 */
	public static Result exception(String msg) {
		return Result.exception().setErrors(msg);
	}

	/**
	 * 创建一个带失败信息的result
	 * 
	 * @param reason
	 *            失败原因
	 * @return result实例
	 */
	public static Result fail(Object... reason) {
		return Result.me().setOperationState(OperationState.FAIL).setErrors(reason);
	}

	/**
	 * 获取一个result实例
	 * 
	 * @return 一个不携带任何信息的result实例
	 */
	public static Result me() {
		return new Result();
	}

	/**
	 * 创建一个成功结果
	 * 
	 * @return result实例状态为成功无数据携带
	 */
	public static Result success() {
		return Result.me().setOperationState(OperationState.SUCCESS);
	}

	/**
	 * 创建一个成功结果
	 * 
	 * @param data
	 *            需要携带的数据
	 * @return result实例状态为成功数据位传入参数
	 */
	public static Result success(NutMap data) {
		return Result.success().setData(data);
	}

	/**
	 * 操作结果数据 假设一个操作要返回很多的数据 一个用户名 一个产品 一个相关产品列表 一个产品的评论信息列表 我们以key
	 * value形式进行保存，页面获取data对象读取其对于的value即可
	 */
	private NutMap data = new NutMap();

	/**
	 * 带状态的操作 比如登录有成功和失败
	 */
	private OperationState operationState = OperationState.SUCCESS;

	private Object[] errors;

	/**
	 * @return the errors
	 */
	public Object[] getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public Result setErrors(Object... errors) {
		this.errors = errors;
		return this;
	}

	public Result() {
		super();
	}

	public Result(OperationState operationState, NutMap data) {
		super();
		this.operationState = operationState;
		this.data = NutMap.WRAP(data);
	}

	/**
	 * 添加更多的数据
	 * 
	 * @param data
	 *            待添加的数据
	 * @return 结果实例
	 */
	public Result addData(NutMap data) {
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			this.data.put(key, data.get(key));
		}
		return this;
	}

	/**
	 * 添加数据
	 * 
	 * @param key
	 * @param object
	 * @return
	 */
	public Result addData(String key, Object object) {
		if (this.data == null) {
			data = new NutMap();
		}
		data.put(key, object);
		return this;
	}

	/**
	 * 清空结果
	 */
	public Result clear() {
		this.operationState = OperationState.SUCCESS;
		if (data != null) {
			this.data.clear();
		}
		return this;
	}

	public NutMap getData() {
		return data;
	}

	public OperationState getOperationState() {
		return operationState;
	}

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return getOperationState() == OperationState.SUCCESS;
	}

	public Result setData(Map<String, Object> data) {
		this.data = NutMap.WRAP(data);
		return this;
	}

	public Result setOperationState(OperationState operationState) {
		this.operationState = operationState;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this, JsonFormat.forLook());
	}
}
