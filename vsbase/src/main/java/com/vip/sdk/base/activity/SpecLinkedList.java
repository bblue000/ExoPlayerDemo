package com.vip.sdk.base.activity;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*package*/ class SpecLinkedList<E> extends AbstractCollection<E> {

	private static class Entry<E> {
		E element;
		Entry<E> next;
		Entry<E> previous;

		Entry(E element, Entry<E> next, Entry<E> previous) {
			this.element = element;
			this.next = next;
			this.previous = previous;
		}
	}
	
	private transient final Entry<E> mHeader = new Entry<E>(null, null, null);
	private transient int mSize = 0;
	public SpecLinkedList() {
		mHeader.next = mHeader.previous = mHeader;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <br/><br/>
	 * 
	 * 从栈顶到栈底的一个迭代器
	 */
	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}
	
	private class Itr implements Iterator<E> {
		private int index;
		private boolean removed = false;
		public Itr() {
			index = size();
		}
		
		@Override
		public boolean hasNext() {
			return index > 0;
		}

		@Override
		public E next() {
			index --;
			removed = false;
			return entryOf(index).element;
		}

		@Override
		public void remove() {
			if (removed) {
				throw new IllegalStateException();
			}
			try {
				SpecLinkedList.this.remove(index);
				removed = true;
			} catch (Exception e) {
				throw new RuntimeException(
						"Have you invoked next() method first? or "
						+ "Is the item at index = " + index + " has removed ?");
			}
		}
	}

	@Override
	public synchronized boolean contains(Object object) {
		return indexOfFromBottom(object) != -1;
	}

	@Override
	public synchronized boolean remove(Object object) {
		if (null == object) {
            for (Entry<E> e = mHeader.next; e != mHeader; e = e.next) {
                if (e.element==null) {
                    remove(e);
                    return true;
                }
            }
        } else {
            for (Entry<E> e = mHeader.next; e != mHeader; e = e.next) {
                if (object.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
		return false;
	}
	
	public synchronized E remove(int index) {
		return remove(entryOf(index));
	}
	
	/**
	 * 相当于调用removeFrom(index, true)
	 * 
	 * @see {@link #removeFrom(int, boolean)}
	 */
	public synchronized SpecLinkedList<E> removeFrom(int index) {
        return removeFrom(index, true);
	}
	
	public synchronized SpecLinkedList<E> removeFrom(int index, boolean removeIndexed) {
		SpecLinkedList<E> sub = new SpecLinkedList<E>();
		Entry<E> target = entryOf(index);
		Entry<E> targetPre = target.previous;
		Entry<E> stoppedOne;
		if (removeIndexed) {
			stoppedOne = targetPre;
		} else {
			// if not delete the specific Entry, use this
			stoppedOne = target;
		}
		
		Entry<E> e = mHeader.previous;
		final int size = size() - index;
        while (e != mHeader && e != stoppedOne) {
            Entry<E> previous = e.previous;
            e.next = e.previous = null;
            sub.add(0, e.element);
            e.element = null;
            e = previous;
        }
        mHeader.previous = targetPre;
        targetPre.next = mHeader;
        mSize -= size;
        return sub;
	}
	
	/**
	 * remove target Entry from the stack list
	 */
	private E remove(Entry<E> e) {
		if (e == mHeader)
			throw new NoSuchElementException();
		E result = e.element;
		e.previous.next = e.next;
		e.next.previous = e.previous;
		e.next = e.previous = null;
		e.element = null;
		mSize--;
		return result;
	}

	public synchronized void clear() {
		Entry<E> e = mHeader.next;
        while (e != mHeader) {
            Entry<E> next = e.next;
            e.next = e.previous = null;
            e.element = null;
            e = next;
        }
        mHeader.next = mHeader.previous = mHeader;
        mSize = 0;
	}

	private Entry<E> addBefore(E e, Entry<E> entry) {
		Entry<E> newEntry = new Entry<E>(e, entry, entry.previous);
		newEntry.previous.next = newEntry;
		newEntry.next.previous = newEntry;
		mSize++;
		return newEntry;
	}
	
	/**
     * Returns the indexed entry.
     */
    private Entry<E> entryOf(int index) {
		if (index < 0 || index >= mSize)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ mSize);
		Entry<E> e = mHeader;
		if (index < (mSize >> 1)) {
			for (int i = 0; i <= index; i++)
				e = e.next;
		} else {
			for (int i = mSize; i > index; i--)
				e = e.previous;
		}
		return e;
    }

	public synchronized int size() {
		return mSize;
	}
	
	public synchronized boolean isEmpty() {
		return size() == 0;
	}

	public Object[] toArray() {
		Object[] result = new Object[mSize];
        int i = 0;
        for (Entry<E> e = mHeader.next; e != mHeader; e = e.next)
            result[i++] = e.element;
        return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < mSize) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), mSize);
		}
		int i = 0;
		Object[] result = a;
		for (Entry<E> e = mHeader.next; e != mHeader; e = e.next)
			result[i++] = e.element;

		if (a.length > mSize)
			a[mSize] = null;
		return a;
	}

	// as a Stack
	public synchronized E get(int index) {
		return entryOf(index).element;
	}
	
	public synchronized SpecLinkedList<E> subList(int index) {
        return subList(index, mSize);
	}
	
	public synchronized SpecLinkedList<E> subList(int fromIndex, int toIndex) {
		SpecLinkedList<E> sub = new SpecLinkedList<E>();
		Entry<E> targetFirst = entryOf(fromIndex);
		Entry<E> stoppedOne = targetFirst.previous;
		Entry<E> targetLast = (toIndex >= mSize) ? mHeader.previous : entryOf(toIndex);
		Entry<E> e = targetLast;
        while (e != mHeader && e != stoppedOne) {
            Entry<E> previous = e.previous;
            sub.add(0, e.element);
            e = previous;
        }
        return sub;
	}
	/**
	 * @return E the peeked item in the stack
	 */
	public synchronized E peek() {
		if (isEmpty())
			throw new NoSuchElementException();
		return entryOf(size() - 1).element;
	}
	
	/**
	 * @return E the popped top item in the stack
	 */
	public synchronized E pop() {
		return remove(isEmpty() ? mHeader : entryOf(size() - 1));
	}
	
	/**
	 * 从栈顶开始查找，找到第一个时，从该索引处开始删除该索引之上的所有项
	 */
	public synchronized SpecLinkedList<E> searchFromTopAndPop(Object o) {
		int index = indexOfFromTop(o);
		if (index < 0) {
			return null;
		}
		return removeFrom(index, true);
	}
	
	/**
	 * 从栈底开始查找，找到第一个时，从该索引处开始删除该索引之上的所有项
	 */
	public synchronized SpecLinkedList<E> searchFromBottomAndPop(Object o) {
		int index = indexOfFromBottom(o);
		if (index < 0) {
			return null;
		}
		return removeFrom(index, true);
	}
	
	/**
     * Pushes an item onto the top of this stack.
     *
     * @param item the item to be pushed onto this stack.
     * @return the <code>item</code> argument.
     */
    public synchronized E push(E item) {
    	addBefore(item, mHeader);
		return item;
    }
    
    @Override
    public synchronized boolean add(E e) {
    	addBefore(e, mHeader);
		return true;
    }
    
    public synchronized boolean add(int index, E item) {
    	addBefore(item, (index == mSize) ? mHeader : entryOf(index));
    	return true;
    }
    
    /**
     * search from 0 index
     * @param o object searching for
     * @return index of the object in the stack list. -1 if not found
     */
    public synchronized int indexOfFromBottom(Object o) {
    	if (isEmpty()) {
    		return -1;
    	}
    	int index = 0;
        if (o == null) {
            for (Entry<E> e = mHeader.next; e != mHeader; e = e.next) {
                if (e.element == null)
                    return index;
                index++;
            }
        } else {
            for (Entry<E> e = mHeader.next; e != mHeader; e = e.next) {
                if (o.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }
    
    /**
     * search from top
     * @param o object searching for
     * @return index of the object in the stack list. -1 if not found
     */
    public synchronized int indexOfFromTop(Object o) {
    	if (isEmpty()) {
    		return -1;
    	}
    	int index = size() - 1;
        if (o == null) {
            for (Entry<E> e = mHeader.previous; e != mHeader; e = e.previous) {
                if (e.element == null)
                    return index;
                index--;
            }
        } else {
            for (Entry<E> e = mHeader.previous; e != mHeader; e = e.previous) {
                if (o.equals(e.element))
                    return index;
                index--;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("[");
    	Iterator<E> ite = iterator();
    	while (ite.hasNext()) {
    		sb.append(ite.next());
    		if (ite.hasNext()) {
    			sb.append(", ");
    		}
		}
//    	Entry<E> e = mHeader.next;
//    	while(e != mHeader) {
//    		sb.append(e.element);
//    		e = e.next;
//    		if (e != mHeader) {
//    			sb.append(", ");
//    		}
//    	}
    	sb.append("]");
    	return sb.toString();
    }
}