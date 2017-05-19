package free.free.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Author:  Jact
 * Date:    2016/2/13.
 * Description:
 */
public class ToastUtils {
    private static Toast mToast;

    /**
     * Long Toast
     * @param context
     */
//    public static void LongToast(Context context, String msg){
//        SuperToast superToast = new SuperToast(context);
//        superToast.setText(msg);
//        superToast.setDuration(SuperToast.Duration.LONG);
//        superToast.show();
//    }

    /**
     * Long Toast
     * @param context
     */
//    public static void ShortToast(Context context, String msg){
//        SuperToast superToast = new SuperToast(context);
//        superToast.setText(msg);
//        superToast.setDuration(SuperToast.Duration.SHORT);
//        superToast.show();
//    }


    public static void shortToast(Context context, String msg) {
        if (mToast == null) {
//            View v = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
//            TextView textView = (TextView) v.findViewById(R.id.textview1);
//            textView.setText(msg);
//            mToast = new Toast(context);
//            mToast.setDuration(Toast.LENGTH_SHORT);
//            mToast.setView(v);

            mToast = Toast.makeText(context,
                    msg,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
