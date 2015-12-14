package com.vip.sdk.base.adapter;

/**
 * 当{@link AbsDataAdapter}的子类，通过{@link AdapterChangeable}
 * 接口中的方法改变数据时触发。
 * @author YinYong
 * @version 1.0
 */
public interface OnDataSetChangedLisener {

	/**
	 * 通知外界内部数据的个数发生了变化。（即使个数没有变也会触发，这是一个数据变化的监听）
	 * @param oldCount 旧的数据个数
	 * @param newCount 新的数据个数
	 */
	void OnDataSetChanged(int oldCount, int newCount);
	
}
