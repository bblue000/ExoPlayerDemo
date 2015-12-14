package com.vip.sdk.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@inheritDoc}
 * 
 * 基于layout xml的Adapter基类，子类需要提供layou ID及必要方法即可
 * 
 * @author YinYong
 * @version 1.0
 * @param <D>
 * @param <H>
 */
public abstract class AbsResLayoutAdapter<D, H> extends AbsDataAdapter<D, H>{

	private final int mLayoutId;
	/**
	 * @param context we recommend you to use an activity context
	 */
	public AbsResLayoutAdapter(Context context) {
		super(context);
		mLayoutId = provideLayoutResId();
	}
	
	/**
	 * provide a layout resource ID from outside(in sub classes)
	 */
	protected abstract int provideLayoutResId();

    @Override
	public final View newView(Context context, int position, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(mLayoutId, parent, false);
	}
	
}
