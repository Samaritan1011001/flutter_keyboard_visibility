package com.github.adee42.keyboardvisibility;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.content.Context;
import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


public class KeyboardVisibilityPlugin implements StreamHandler, Application.ActivityLifecycleCallbacks, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String STREAM_CHANNEL_NAME = "github.com/adee42/flutter_keyboard_visibility";
    EventSink eventsSink;
    boolean isVisible;
    View mainView;
    private Context mContext;


    KeyboardVisibilityPlugin(Registrar registrar) {
                
        mContext = registrar.context().getApplicationContext();
        eventsSink = null;
        if (registrar.activity() != null) {
        mainView = ((ViewGroup) registrar.activity().findViewById(android.R.id.content)).getChildAt(0);
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();

        mainView.getWindowVisibleDisplayFrame(r);

        // check if the visible part of the screen is less than 85%
        // if it is then the keyboard is showing
        boolean newState = ((double)r.height() / (double)mainView.getRootView().getHeight()) < 0.85;

        if (newState != isVisible) {
            isVisible = newState;
            if (eventsSink != null) {
                eventsSink.success(isVisible ? 1 : 0);
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        mainView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public static void registerWith(Registrar registrar) {

        final EventChannel eventChannel = new EventChannel(registrar.messenger(), STREAM_CHANNEL_NAME);
        KeyboardVisibilityPlugin instance = new KeyboardVisibilityPlugin(registrar);
        eventChannel.setStreamHandler(instance);
    }

    @Override
    public void onListen(Object arguments, final EventSink eventsSink) {
        // register listener
        this.eventsSink = eventsSink;

        // is keyboard is visible at startup, let our subscriber know
        if (isVisible) {
            eventsSink.success(1);
        }
    }

    @Override
    public void onCancel(Object arguments) {
        eventsSink = null;
    }
}
