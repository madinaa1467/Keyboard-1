package io.ionic.keyboard;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
// import additionally required classes for calculating screen height
import android.view.Display;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

public class IonicKeyboard extends CordovaPlugin {

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("close".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    //http://stackoverflow.com/a/7696791/1091751
                    InputMethodManager inputManager = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View v = cordova.getActivity().getCurrentFocus();

                    if (v == null) {
                        callbackContext.error("No current focus");
                    } else {
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        callbackContext.success(); // Thread-safe.
                    }
                }
            });
            return true;
        }
        if ("show".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    ((InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    callbackContext.success(); // Thread-safe.
                }
            });
            return true;
        }
        if ("init".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    //calculate density-independent pixels (dp)
                    //http://developer.android.com/guide/practices/screens_support.html
                    DisplayMetrics dm = new DisplayMetrics();
                    cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    final float density = dm.density;

                    //http://stackoverflow.com/a/4737265/1091751 detect if keyboard is showing
                    final View rootView = cordova.getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
////                            Rect r = new Rect();
////                            //r will be populated with the coordinates of your view that area still visible.
////                            rootView.getWindowVisibleDisplayFrame(r);
////
////                            Rect rectgle= new Rect();
////                            window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
////                            int StatusBarHeight= (int)(rectgle.top/density);
//
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////                                DisplayMetrics metrics = new DisplayMetrics();
////                                getWindowManager().getDefaultDisplay().getMetrics(metrics);
////                                int usableHeight = metrics.heightPixels;
////                                getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
////                                int realHeight = metrics.heightPixels;
////                                if (realHeight > usableHeight)
////                                    int StatusBarHeight = realHeight - usableHeight;
////                                else
////                                    int StatusBarHeight = 0;
////                            }
//
                    OnGlobalLayoutListener list = new OnGlobalLayoutListener() {
                        int previousHeightDiff = 0;
                        int StatusBarHeight = 0;
                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            //r will be populated with the coordinates of your view that area still visible.
                            rootView.getWindowVisibleDisplayFrame(r);

                            PluginResult result;

                            // cache properties for later use
//                            int rootViewHeight = rootView.getRootView().getHeight();
//                            int resultBottom = r.bottom;
//
//                            // calculate screen height differently for android versions >= 21: Lollipop 5.x, Marshmallow 6.x
//                            //http://stackoverflow.com/a/29257533/3642890 beware of nexus 5
//                            int screenHeight;
//
//                            if (Build.VERSION.SDK_INT >= 21) {
//                                Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
//                                Point size = new Point();
//                                display.getSize(size);
//                                screenHeight = size.y;
//                            } else {
//                                screenHeight = rootViewHeight;
//                            }
//                            int heightDiff = screenHeight - resultBottom;


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                DisplayMetrics metrics = new DisplayMetrics();
//                                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                                int usableHeight = metrics.heightPixels;
//                                getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
//                                int realHeight = metrics.heightPixels;
//                                if (realHeight > usableHeight) { StatusBarHeight = realHeight - usableHeight;}
//                                else { StatusBarHeight = 0;}

                                StatusBarHeight = 0;
                            }

                            int heightDiff = rootView.getRootView().getHeight() - r.bottom;

                            int pixelHeightDiff = (int)(heightDiff / density);
                            if (pixelHeightDiff > 100 && pixelHeightDiff != previousHeightDiff) { // if more than 100 pixels, its probably a keyboard...
                                String msg = "S" + Integer.toString(pixelHeightDiff-StatusBarHeight);
                                result = new PluginResult(PluginResult.Status.OK, msg);
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            }
                            else if ( pixelHeightDiff != previousHeightDiff && ( previousHeightDiff - pixelHeightDiff ) > 100 ){
                                String msg = "H";
                                result = new PluginResult(PluginResult.Status.OK, msg);
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            }
                            previousHeightDiff = pixelHeightDiff;
                        }
                    };


                    rootView.getViewTreeObserver().addOnGlobalLayoutListener(list);


                    PluginResult dataResult = new PluginResult(PluginResult.Status.OK);
                    dataResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(dataResult);
                }
            });
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }


}


