package ren.solid.skinloader.attr;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ren.solid.skinloader.load.SkinManager;
import ren.solid.skinloader.util.L;

/**
 * Created by user90 on 2016/7/15.
 */
public class ProgressDrawableAttr extends SkinAttr  {

    String TAG="ProgressDrawableAttr";

    @Override
    public void apply(View view) {
        if (view instanceof ProgressBar) {
            Log.i(TAG, "apply");
            ProgressBar progressBar = (ProgressBar) view;
           if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
                Log.i(TAG, "apply drawable");
                //  tv.setDivider(SkinManager.getInstance().getDrawable(attrValueRefId));
               progressBar.setProgressDrawable(SkinManager.getInstance().getDrawable(attrValueRefId));
            }
        }

    }
}
