package ren.solid.skinloader.attr;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import ren.solid.skinloader.load.SkinManager;

/**
 * Created by user90 on 2016/7/15.
 */
public class ListViewDividerAttr extends SkinAttr  {

    String TAG="ListViewDividerAttr";

    @Override
    public void apply(View view) {
        if (view instanceof ListView) {
            Log.i(TAG, "apply");
            ListView listView = (ListView) view;
           if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                Log.i(TAG, "apply color");
//               listView.setDivider(SkinManager.getInstance().getDrawable(attrValueRefId));
               listView.setDivider(new ColorDrawable(SkinManager.getInstance().getColor(attrValueRefId)));
//               setDividerHeight是必须的
               listView.setDividerHeight(dip2px(0.5f));
            }
        }

    }

    /**
     * dip转换px
     */
    public static int dip2px(float dip) {
        final float scale = SkinManager.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
