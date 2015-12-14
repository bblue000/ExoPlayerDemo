package com.vip.sdk.base.adapter;

import java.util.Collection;
import java.util.Map;

/**
 * 指示子类是可以改变数据的Adapter，并提供可以改变数据的方法
 * 
 * @author yong01.yin
 *
 * @param <T>
 */
interface AdapterChangeable<T> {
	
	/**
	 * set the primary adapter data, and then we do not call notifyDataSetChanged,
	 * because this is a complex case, we can not guess what will
	 * be called, notifyDataSetChanged or notifyDataSetInvalidate
	 * @param c new data set into the Adapter
	 */
	AdapterChangeable<T> setData(Collection<T> c);
	
	/**
	 * remove the specific item of data from, then refresh adapter view,
	 * and then should call notifyDataSetChanged
	 * @param position position of the item in the adapter view 
	 */
	AdapterChangeable<T> removeData(int position);
	
	/**
	 * remove the specific item of data from, then refresh adapter view,
	 * and then should call notifyDataSetChanged
	 * @param from position of the item in the adapter view, included
	 * @param to position of the item in the adapter view, excluded
	 */
	AdapterChangeable<T> removeData(int from, int to);
	
	/**
	 * append single item to the AdapterView, and then should call notifyDataSetChanged
	 * @param t new data to append
	 */
	AdapterChangeable<T> appendData(T t);
	
	/**
	 * append a collection of data to the AdapterView, and then should call notifyDataSetChanged
	 * @param c new data to append
	 */
	AdapterChangeable<T> appendDataList(Collection<T> c);
	
	/**
	 * append a Map of data to the AdapterView, and then should call notifyDataSetChanged
	 * @param m new data Map to append
	 */
	AdapterChangeable<T> appendDataMap(Map<?, T> m);
	
	
	
	/**
	 * pre-append single item to the AdapterView, and then should call notifyDataSetChanged
	 * @param t new data to append
	 */
	AdapterChangeable<T> prependData(T t);
	
	/**
	 * pre-append a collection of data to the AdapterView, and then should call notifyDataSetChanged
	 * @param c new data to append
	 */
	AdapterChangeable<T> prependDataList(Collection<T> c);
	
	/**
	 * pre-append a Map of data to the AdapterView, and then should call notifyDataSetChanged
	 * @param m new data Map to append
	 */
	AdapterChangeable<T> prependDataMap(Map<?, T> m);
	
	
	/**
	 * update the specific position item, and then should call notifyDataSetChanged
	 * @param position target position
	 * @param t new data to update
	 */
	AdapterChangeable<T> update(int position, T t);
	
	/**
	 * update items from the specific position with data in collection c, 
	 * and then should call notifyDataSetChanged
	 * @param fromPos target position from which to update
	 * @param c new data Collection to update
	 */
	AdapterChangeable<T> update(int fromPos, Collection<T> c);
}
