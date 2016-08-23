package com.fusi.fishingalarm.utils;

import android.app.Dialog;
import android.net.Uri;
import android.util.Log;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by user90 on 2016/6/1.
 */
public class SocketUtil {

    private String TAG="SocketUtil";
    /**
     * 返回时回调接口
     * dealWithJson 处理返回json
     * dealWithError 处理返回错误
     *
     */
    public interface SocketCallback{
        public void dealWithMessage(String  command, String data);
        public void dealWithError(String  command,String data);
    }

//    /**
//     * 此方法默认get请求
//     * @param url 调用接口方法
//     * @param map 上传参数
//     * @param callback 服务器端返回参数回调接口
//     */
//    public static void uploadNoFile(String url, Map<String,String> map, final VolleyCallback callback){
//        uploadNoFile(GET,url,map,callback);
//    }

//    /**
//     * @param command 调用接口方法
//     * @param data 上传参数
//     * @param callback 服务器端返回参数回调接口
//     * @param timeout  设置连接超时
//     */
//    public static void sendMsg(String command,String data,final SocketCallback callback,int timeout,boolean shouldShowLoadingView){
//        final Dialog dialog;
//        if(shouldShowLoadingView){
//            dialog=UIUtils.showDialog(UILApplication.getCurrentContext(), R.layout.loading_layout,true);
//        }else{
//            dialog=null;
//        }
//        DefaultRetryPolicy retry=null;
//        if(timeout!=-1){
//            retry=new DefaultRetryPolicy(timeout, 3, 1);
//        }
//        final String method=url;
//        RequestQueue queue = MyVolley.getRequestQueue();
//        queue.cancelAll(method);
//        StringRequest request=null;
//        if(type==GET){
//            Uri.Builder builder = Uri.parse(UILApplication.URL+method).buildUpon();
//            for(String key:map.keySet()){
//                builder.appendQueryParameter(key, map.get(key));
//            }
//            request= new StringRequest(builder.toString(), new Listener<String>() {
//                @Override
//                public void onResponse(String arg0) {
//                    // TODO Auto-generated method stub
//                    Log.i(TAG,"onresponse:"+arg0);
//                    dismissDialog(dialog);
//                    callback.dealWithJson(method,arg0);
//                }
//            }, new ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError arg0) {
//                    // TODO Auto-generated method stub
//                    dismissDialog(dialog);
//                    Log.i(TAG,"volleyerror: "+arg0.getMessage());
//                    callback.dealWithError(method, ExceptionUtils.getExceptionMsg(arg0));
//                }
//
//            });
//
//        }else if(type==POST){
//            request = new StringRequest(Method.POST,UILApplication.URL+method,new Listener<String>() {
//                @Override
//                public void onResponse(String arg0) {
//                    // TODO Auto-generated method stub
//                    Log.i(TAG,"onresponse:"+arg0);
//                    dismissDialog(dialog);
//                    callback.dealWithJson(method,arg0);
//                }
//            }, new ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError arg0) {
//                    // TODO Auto-generated method stub
//                    dismissDialog(dialog);
//                    Log.i(TAG,"volleyerror: "+arg0.getMessage());
//                    callback.dealWithError(method, ExceptionUtils.getExceptionMsg(arg0));
//                }
//
//            });
//            request.setParams(map);
//        }
//        if(retry!=null)	 request.setRetryPolicy(retry);
//        request.setShouldCache(false);
//        request.setTag(method);
//        queue.add(request);
//    }
//
//
//    private static void dismissDialog(final Dialog dialog){
//        if(dialog==null) return;
//        UILApplication.getMainThreadHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//            }
//        });
//    }
//
//    /**
//     *
//     * @param type 请求类型（POST，GET）
//     * @param url 调用接口方法
//     * @param map 上传参数
//     * @param callback 服务器端返回参数回调接口
//     */
//    public static void uploadNoFile(int type,String url,Map<String,String> map,final VolleyCallback callback){
//        uploadNoFile(type, url, map, callback, -1,true);
//    }
//
//    /**
//     *
//     * @param url 调用接口方法
//     * @param paramMap 上传参数（不包括文件）
//     * @param filesMap 上传文件
//     * @param fileArrayMap 上传文件数组
//     * @param callback 服务器端返回参数回调接口
//     */
//    public static void uploadWithFile(String url, Map<String,String> paramMap, Map<String,String> filesMap, Map<String,ArrayList<String>> fileArrayMap, final VolleyCallback callback){
//        final String method=url;
//        RequestQueue queue = MyVolley.getRequestQueue();
//        queue.cancelAll(method);
//        SimpleMultiPartRequest request=new SimpleMultiPartRequest(UILApplication.URL+method,new Listener<String>() {
//            @Override
//            public void onResponse(String arg0) {
//                // TODO Auto-generated method stub
//                Log.i(TAG,"onresponse:"+arg0);
//                if(callback!=null) callback.dealWithJson(method,arg0);
//            }
//        }, new ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError arg0) {
//                // TODO Auto-generated method stub
//
//                Log.i(TAG,"volleyerror: "+arg0.getMessage());
//                if(callback!=null) callback.dealWithError(method, ExceptionUtils.getExceptionMsg(arg0));
//            }
//
//        });
//        if(filesMap!=null&&filesMap.size()>0){
//            for(String fileKey:filesMap.keySet()){
//                request.addFile(fileKey, filesMap.get(fileKey));
//            }
//        }
//        if(fileArrayMap!=null&&fileArrayMap.size()>0){
//            for(String fileArrayKey:fileArrayMap.keySet()){
//                request.addFileArray(fileArrayKey, fileArrayMap.get(fileArrayKey));
//            }
//        }
//        request.addMultipartParam(paramMap);
//        request.setTag(method);
//        queue.add(request);
//    }

}
