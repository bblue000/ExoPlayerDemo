package com.vip.sdk.base.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * &lt;工具类&gt; 提供一些View操作相关的方法
 * 
 * @author Yin Yong
 *
 */
public class ViewUtils {

	private ViewUtils() { }
	
	private static final ThreadLocal<Rect> sRectPool = new ThreadLocal<Rect>() {
		@Override
		protected Rect initialValue() {
			return new Rect();
		}
		
		public Rect get() {
			Rect rect = super.get();
			rect.setEmpty();
			return rect;
		};
	};
	@SuppressWarnings("deprecation")
	public static RelativeLayout newTransparentRelativeLayout(Context context) {
		RelativeLayout layout = new RelativeLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		layout.setBackgroundDrawable(null);
		return layout;
	}
	
	public static void setViewVisibility(View view, int visibility) {
		if (null == view) {
			return;
		}
		if (view.getVisibility() != visibility) {
			view.setVisibility(visibility);
		}
	}

	public static void setViewVisibility(View view, boolean show) {
		if (null == view) {
			return;
		}
		if (show) {
            setViewVisible(view);
		} else {
            setViewGone(view);
        }
	}

	public static boolean isVisibilityEqual(View view, int visibility) {
		if (null == view) {
			return false;
		}
		return view.getVisibility() == visibility;
	}
	
	public static boolean isVisible(View view) {
		return isVisibilityEqual(view, View.VISIBLE);
	}
	
	public static boolean isInvisible(View view) {
		return isVisibilityEqual(view, View.INVISIBLE);
	}
	
	public static boolean isGone(View view) {
		return isVisibilityEqual(view, View.GONE);
	}
	
	public static void setViewVisible(View view) {
		setViewVisibility(view, View.VISIBLE);
	}
	
	public static void setViewInvisible(View view) {
		setViewVisibility(view, View.INVISIBLE);
	}
	
	public static void setViewGone(View view) {
		setViewVisibility(view, View.GONE);
	}
	
	public static int maxWidthOfView() {
		return (0x1 << 30) - 1;
	}
	
	public static int maxHeightOfView() {
		return (0x1 << 30) - 1;
	}
	
	public static Rect getViewScreenRect(View view) {
		Rect rect = sRectPool.get();
		if (null != view) {
			view.getGlobalVisibleRect(rect);
		}
		return rect;
	}

    public static void setText(EditText editText, CharSequence text) {
		if (null == editText) {
			return ;
		}
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

	public static void setOnClickListener(View view, View.OnClickListener listener) {
		if (null == view) {
			return ;
		}
		view.setOnClickListener(listener);
	}
}
