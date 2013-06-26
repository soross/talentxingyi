package org.talentware.android.view;import android.content.Context;import android.view.View;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.Toast;import org.talentware.android.TalentWareApp;public class ToastManager {    /**     * 显示Toast文字     *     * @param aContext       上下文     * @param aText          显示文字     * @param aToastTimeCost 显示时长     */    public static void toastText(final Context aContext, final String aText, final int aToastTimeCost) {        Toast.makeText(aContext, aText, aToastTimeCost).show();    }    /**     * 在指定位置（包含有偏移量）显示Toast文字     *     * @param aContext       上下文     * @param aText          Toast文字     * @param aGravity       位置     * @param aX             x轴偏移量     * @param aY             y轴偏移量     * @param aToastTimeCost 显示时长     */    public static void toastText(final Context aContext, final String aText, final int aGravity, final int aX, final int aY, final int aToastTimeCost) {        Toast toast = Toast.makeText(aContext, aText, aToastTimeCost);        toast.setGravity(aGravity, aX, aY);        toast.show();    }    /**     * 在指定位置（包含有偏移量）显示带有图片的Toast文字     *     * @param aContext       上下文     * @param aText          Toast文字     * @param aGravity       位置     * @param aX             x轴偏移量     * @param aY             y轴偏移量     * @param aToastTimeCost 显示时长     */    public static void toastText(final Context aContext, final String aText, final int aDrawable, final int aGravity, final int aX, final int aY, final int aToastTimeCost) {        Toast toast = Toast.makeText(aContext, aText, aToastTimeCost);        toast.setGravity(aGravity, aX, aY);        LinearLayout toastView = (LinearLayout) toast.getView();        ImageView imageCodeProject = new ImageView(TalentWareApp.getAppContext());        imageCodeProject.setImageResource(aDrawable);        toastView.addView(imageCodeProject, 0);        toast.show();    }    /**     * 在指定位置（包含有偏移量）显示自定义布局的Toast文字     *     * @param aContext        上下文     * @param aLayoutID       布局ID     * @param aLayoutCallBack 布局渲染回调     * @param aGravity        位置     * @param aX              x轴偏移量     * @param aY              y轴偏移量     * @param aToastTimeCost  显示时长     */    public static void toastText(final Context aContext, final int aLayoutID, LayoutCallBack aLayoutCallBack, final int aGravity, final int aX, final int aY, final int aToastTimeCost) {        // LayoutInflater inflater = ((Activity) aContext).getLayoutInflater();        View layout = null;        // 此处先注释掉        // View layout = inflater.inflate(aLayoutID, (ViewGroup) ((Activity) aContext).findViewById(R.id.ll_toast_root));        aLayoutCallBack.onLayoutCallBack();        Toast toast = new Toast(aContext);        toast.setGravity(aGravity, aX, aY);        toast.setDuration(aToastTimeCost);        toast.setView(layout);        toast.show();    }    /**     * 布局回调接口方法     */    public interface LayoutCallBack {        public void onLayoutCallBack();    }}