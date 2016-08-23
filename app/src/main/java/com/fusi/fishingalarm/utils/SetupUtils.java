package com.fusi.fishingalarm.utils;

import android.text.TextUtils;

public class SetupUtils {

	public static String getUser_name() {
        if(SharedPrefrenceUtils.getBoolean(UIUtils.getContext(),"isThirdAuth")){
            return SharedPrefrenceUtils.getString(UIUtils.getContext(),"third_nick");
        }else{
            return SharedPrefrenceUtils.getString(UIUtils.getContext(),"nick");
        }
	}
//手机号码
	public static String getMobile() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "username"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"username");
		}
		return null;
	}

	public static String getUser_avatar() {
        if(SharedPrefrenceUtils.getBoolean(UIUtils.getContext(),"isThirdAuth")){
            return SharedPrefrenceUtils.getString(UIUtils.getContext(),"third_avatar");
        }else{
            return SharedPrefrenceUtils.getString(UIUtils.getContext(),"avatar");
        }
	}

	public static String getUser_cAvatar() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "cAvatar"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"cAvatar");
		}
		return null;
	}
	public static String getLevel() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "level"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"level");
		}
		return null;
	}
	public static String getCredit() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "credit"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"credit");
		}
		return null;
	}
	public static String getNextCredit() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "nextCredit"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"nextCredit");
		}
		return null;
	}

	public static boolean hasChangeAvatar() {
		if (SharedPrefrenceUtils.getBoolean(
				UIUtils.getContext(), "hasChangeAvatar")) {
			return SharedPrefrenceUtils.getBoolean(UIUtils.getContext(),
					"hasChangeAvatar");
		}

		return false;
	}

}
