package com.vip.sdk.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import com.androidquery.AbstractAQuery;
import com.vip.sdk.base.encoder.Base64;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author simon
 */
public class Utils {

    public final static String D = ",";
    public static final String SYMBOL_EMPTY = "";
    /**
     * 没有网络
     */
    public static final int NETWORKTYPE_INVALID = 0;
    /**
     * wap
     */
    public static final int NETWORKTYPE_WAP = 1;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /*
     * public static <T extends AbstractAQuery<T>> void loadImageEx(final T aq, final Context context, String big_url,
     * final int presetID) { if (aq != null) { MyLog.error(Utils.class, "Load Image for small:" + big_url);
     * aq.image(big_url, true, true, 0, presetID, new BitmapAjaxCallback() {
     * 
     * @SuppressLint("NewApi")
     * 
     * @Override protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
     * 
     * if (bm != null) { if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1 &&
     * bm.getByteCount() < 1000) || (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR1 &&
     * bm.getRowBytes() * bm.getHeight() < 1000)) { MyLog.error(Utils.class, "Load big Image failed"); } else {
     * super.callback(url, iv, bm, status); } } else { MyLog.error(Utils.class, "Load big Image failed"); } } }); } }
     */
    /**
     * 2G网络
     */
    public static final int NETWORKTYPE_2G = 2;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final int NETWORKTYPE_3G = 3;
    /**
     * wifi网络
     */
    public static final int NETWORKTYPE_WIFI = 4;
    private static final Pattern PATTERN = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:]+");
    public static String NETWORT_2G = "2G";
    public static String NETWORT_3G = "3G";
    public static String NETWORT_4G = "4G";
    public static String NETWORT_WIFI = "WIFI";
    public static char[]
            letterArray =
            {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
            };


    /**
     * 对象不为空
     */
    public static boolean notNull(Object obj) {
        if (null != obj && obj != "") {
            return true;
        }
        return false;
    }

    public static boolean isNull(Object obj) {
        if (null == obj || obj == "" || obj.equals("")) {
            return true;
        }
        return false;
    }

    public static int getScreenWidth(Context con) {
        return con.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context con) {
        return con.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 给图片补全地址
     */
    public static String getImageUrl(String url) {
        if (null == url) {
            return null;
        }
        if (isURL(url)) {
            return url;
        }
        return url;
    }

    /**
     * 判断字符串是否为URL
     */
    public static boolean isURL(String url) {
        if (!TextUtils.isEmpty(url) && PATTERN.matcher(url).find()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取网络状态，wifi,wap,2g,3g.
     *
     * @param context 上下文
     * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G}, {@link
     * #NETWORKTYPE_INVALID}, {@link #NETWORKTYPE_WAP}* <p> {@link #NETWORKTYPE_WIFI}
     */

    public static int getNetWork(Context context) {
        int mNetWorkType = NETWORKTYPE_WIFI;
        ConnectivityManager
                manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();

                mNetWorkType =
                        TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context)
                                ? NETWORKTYPE_3G : NETWORKTYPE_2G)
                                : NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }

        return mNetWorkType;
    }

    public static String getNetWorkTypeDescription(Context context) {
        String mNetWorkType = NETWORT_WIFI;
        ConnectivityManager
                manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase(NETWORT_WIFI)) {

                mNetWorkType = networkInfo.getTypeName();
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();

                mNetWorkType =
                        TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORT_3G
                                : NETWORT_2G)
                                : NETWORT_2G;
            }
        }
        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager
                telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    // 判断网络类型
    public static boolean isWap() {
        String proxyHost = android.net.Proxy.getDefaultHost();
        if (proxyHost != null) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomLetters(int n) {
        Random rd = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < n; i++)// 随即10个拿出来看看
        {
            buffer.append(letterArray[rd.nextInt(52)]);
        }

        return buffer.toString();
    }

    // password转码 1.App调用中间层接口时，请对password进行加密，加密规则为在3/5/7/9位均插入一数字或字母,加上时间戳后做base64的加密。
    public static String getParamPassword(String password) {
        String result = null;
        if (password != null) {

            int i = 0;

            // 再加时间戳
            password += System.currentTimeMillis() / 1000;

            // 在第2位插入随机值
            if (password.length() >= 3) {
                result = password.substring(0, 2);
                result += Utils.getRandomLetters(1);
                i = 2;
            }

            // 在第4位插入随机值
            if (password.length() >= 5) {
                result += password.substring(2, 4);
                result += Utils.getRandomLetters(1);
                i = 4;
            }

            // 在第6位插入随机值
            if (password.length() >= 7) {
                result += password.substring(4, 6);
                result += Utils.getRandomLetters(1);
                i = 6;
            }

            // 在第8位插入随机值
            if (password.length() >= 9) {
                result += password.substring(6, 8);
                result += Utils.getRandomLetters(1);
                i = 8;
            }

            result += password.substring(i);
        }

        // 再对result进行base64转码
        if (result != null) {
            // data = android.util.Base64.encode(data.getBytes(), android.util.Base64.DEFAULT).toString();
            result = Base64.encodeBytes(result.getBytes());
        }

        return result;
    }

    /**
     * 判断网络是否畅通
     */
    public static boolean isNetworkAvailable(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager
                connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                int l = info.length;
                for (int i = 0; i < l; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //获取一个假的IP地址，用于临时token 获取
    public static String getFakeIp() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(256));
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();

    }

    public static String getService_Provider(Context context) {
        String service_providers = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String op = tm.getSimOperator();
                if (op != null) {
                    if ("46000".startsWith(op) || "46002".startsWith(op)) {
                        service_providers = "CMCC";
                    } else if ("46001".startsWith(op)) {
                        service_providers = "CUCC";
                    } else if ("46003".startsWith(op)) {
                        service_providers = "CTCC";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service_providers;
    }


    /**
     * 对于老接口获取service url参数
     * 对于新接口（restful）获取uri
     *
     * @param url
     * @return
     */
    public static String getService(String url, Map<String, String> params, boolean isPost) {
        String service = null;
        if (isPost && params != null) {
            service = params.get("service");
            if (!TextUtils.isEmpty(service)) {
                return service;
            }
        }

        try {
            if (url == null || url.trim().length() == 0) {
                return null;
            }
//            if (url.startsWith(SdkConfig.self().getApiVipLogUrlPrefix())) {
//                return null;
//            }
            int i = url.indexOf("router.do");
            if (i > -1) {
                //老接口
                URI uri = URI.create(url);
                List<NameValuePair> list = URLEncodedUtils.parse(uri, "UTF-8");
                for (NameValuePair nameValuePair : list) {
                    if ("service".equals(nameValuePair.getName())) {
                        service = nameValuePair.getValue();
                        break;
                    }
                }
            } else {
                //新接口
                URL uri = new URL(url);
                service = uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }


    /**
     * 检查当前网络是2G还是3G,或者wifi
     */
    public static String getNetWorkType(Context context) {

        String netCode = NETWORT_3G;

        WifiManager
                mWifiManager =
                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();

        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {

            netCode = NETWORT_WIFI;

        } else {

            TelephonyManager
                    telMgr =
                    (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);

            int networkType = 0;
            if (telMgr != null) {
                networkType = telMgr.getNetworkType();

                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netCode = NETWORT_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netCode = NETWORT_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netCode = NETWORT_4G;
                        break;
                    default:
                        break;
                }
            }

        }

        return netCode;
    }

    public static String formatDate(long times, String format) {
        Date date = new Date(times);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 复合方式调用AbstractAQuery.image, 简化部分固定参数
     *
     * @param aq       AQuery对象
     * @param url      图片url
     * @param presetID 默认图片id
     * @see com.androidquery.AbstractAQuery#image(String url, boolean memCache, boolean fileCache, int targetWidth,
     * int fallbackId, android.graphics.Bitmap preset, int animId)
     */
    public static <T extends AbstractAQuery<T>> T loadImage(T aq, Context context, String url,
                                                            int presetID) {
        //默认图片管理  暂时不关注 后期可优化 与aquery 为同一块内存管理图片
        //  Bitmap presentBitmap = loadBitmapFromCache(presetID, context);
        if (aq != null) {
            VSLog.error("Load Image:" + url);
            return aq.image(url, true, true);
        }
        return null;
    }

    static InputMethodManager inputMethodManager;

    // 键盘开启
    public static void keyboardOn(Context context) {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 键盘关闭
    public static void keyboardOff(Context context, EditText et) {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
    // 简单判断手机号码正确性
    public static boolean checkPhone(String phone) {
        Pattern pattern = Pattern.compile("1\\d{10}");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static void setTextViewText(TextView textView,String format,final String str){
        if(str!=null && str.length()!=0){
            textView.setText(String.format(format,str));
        }
    }

    public static interface IListItemPropertyGetter<T>{
        public String getInterestProperty(T object);
    }
    /**
    用“，”分割List中每个元素中感兴趣的属性，最后拼接成一个字符串。最后一个元素，不需要追加“，”
     @param list 需要操作的list
     @param itemPropertyGetter 获取List Item中感兴趣的属性的回调函数。此方法的调用者负责提供感兴趣的属性
     */
    public static <T> String appendExtraCommaInListItem(List<T> list, IListItemPropertyGetter<T> itemPropertyGetter){
        if (null == list) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            T object = list.get(i);
            if(null != object){
                sb.append(itemPropertyGetter.getInterestProperty(object));
            }
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static boolean isPhone(String number) {
        if (!TextUtils.isEmpty(number)) {
            Pattern p = Pattern.compile("^(1)\\d{10}$");
            Matcher m = p.matcher(number);
            return m.matches();
        }
        return false;
    }

}
