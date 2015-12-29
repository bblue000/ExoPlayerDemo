package com.vip.sdk.uilib.media.video;

/**
 *
 * 播放状态的回调。
 *
 * 需要用{@link VideoController}中的方法给{@link VIPVideo}设置监听回调。
 *
 * <br/>
 *
 * 特别注意：如果已经开始播放，则在设置时将会调用{@link #STATE_START}。
 *
 * Created by Yin Yong on 15/12/27.
 *
 * @see VideoController#setControlCallback(VIPVideo, VideoControlCallback)
 */
public interface VideoControlCallback {

    // state constants start
    /**
     * 加载资源（全部或部分），此时还不能播放
     */
    int STATE_LOADING = 0;

    /**
     * 加载资源失败（包含错误信息）
     */
    int STATE_LOAD_ERR = STATE_LOADING + 1;

    /**
     * 资源已加载（全部或部分），已经可以播放。
     */
    int STATE_PREPARED = STATE_LOAD_ERR + 1;

    /**
     * 开始播放（一种是从停止或者暂停状态变为播放状态）。
     */
    int STATE_START = STATE_PREPARED + 1;

    /**
     * 由播放状态变为暂停状态
     */
    int STATE_PAUSE = STATE_START + 1;

    /**
     * 播放完成，紧接着会进入{@link #STATE_STOP}状态
     */
    int STATE_COMPLETION = STATE_PAUSE + 1;

    /**
     * 进入停止状态（主动被停止，或者已播放完成）
     */
    int STATE_STOP = STATE_COMPLETION + 1;

    /**
     * {@link android.media.MediaPlayer}内部异步操作时发生错误（包含错误信息），
     * 紧接着会进入{@link #STATE_COMPLETION}。
     *
     * <br/>
     *
     * <ul>
     *     <li>
     *         code: <br/>
     *         the type of error that has occurred:
     *         <ul>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNKNOWN}</li>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_SERVER_DIED}</li>
     *         </ul>
     *     </li>
     *     <li>
     *         extraCode:<br/>
     *         an extra code, specific to the error. Typically implementation dependent.
     *         <ul>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_IO}</li>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_MALFORMED}</li>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNSUPPORTED}</li>
     *             <li>{@link android.media.MediaPlayer#MEDIA_ERROR_TIMED_OUT}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     */
    int STATE_ERR = STATE_STOP + 1;
    // state constants end

    /**
     * 当状态改变时回调
     *
     * @param status 有的状态包含状态信息，将在触发相应状态时给出，参见不同状态说明
     */
    void onStateChanged(VIPVideo video, int state, VideoStatus status);

    /**
     * 加载过程回调
     *
     * @param current 已经加载的大小
     * @param total 总资源大小
     */
    void onLoadProgress(VIPVideo video, String url, long current, long total);

    /**
     *
     * 视频状态的信息
     *
     * <p/>
     * <p/>
     * Created by Yin Yong on 15/12/14.
     *
     * @since 1.0
     */
    class VideoStatus {

        /**
         * 基本包含信息，信息码
         */
        public final int code;

        /**
         * 基本包含信息，信息文本
         */
        public final String message;

        /**
         * 额外信息，默认为0
         */
        public int extraCode;

        public VideoStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public VideoStatus(int code, String message, int extraCode) {
            this.code = code;
            this.message = message;
            this.extraCode = extraCode;
        }

        /**
         * 设置额外的/副的状态信息
         *
         * @return 当前对象
         */
        public VideoStatus extraCode(int extraCode) {
            this.extraCode = extraCode;
            return this;
        }

        @Override
        public String toString() {
            return String.format("State {%d, %d, %s}", code, extraCode, message);
        }
    }
}
