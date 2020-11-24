package com.polyassist.omar.ce301;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.support.design.internal.NavigationMenu;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

//https://www.youtube.com/watch?v=Z4Mu6g_M71s (For the Fab Speed Dial)
//https://codelabs.developers.google.com/codelabs/developing-android-a11y-service/index.html?index=..%2F..index#1 (For the accessibility service)

public class GlobalActionBarService extends AccessibilityService{
    FrameLayout mLayout;

    @Override
    protected void onServiceConnected() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = (FrameLayout) LayoutInflater.from(new ContextThemeWrapper(this, R.style.AppTheme)).inflate(R.layout.action_bar, null);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        wm.addView(mLayout, lp);


        FabSpeedDial fabSpeedDial = (FabSpeedDial) mLayout.findViewById(R.id.accessibleMenu);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {

            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.power) {
                    performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
                }
                else if(menuItem.getItemId() == R.id.volumeup) {
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

                }
                else if(menuItem.getItemId() == R.id.volumedown) {
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
                else if(menuItem.getItemId() == R.id.scroll) {
                    AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
                    if (scrollable != null) {
                        scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
                    }
                }
                else if(menuItem.getItemId() == R.id.swipe) {
                    Path swipePath = new Path();
                    swipePath.moveTo(1000, 1000);
                    swipePath.lineTo(100, 1000);
                    GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                    gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
                    dispatchGesture(gestureBuilder.build(), null, null);

                }

                else if(menuItem.getItemId() == R.id.openApp) {
                    Intent home = new Intent(getApplicationContext(), Home.class);
                    startActivity(home);


                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }

        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private AccessibilityNodeInfo findScrollableNode(AccessibilityNodeInfo root) {
        Deque<AccessibilityNodeInfo> deque = new ArrayDeque<>();
        deque.add(root);
        while (!deque.isEmpty()) {
            AccessibilityNodeInfo node = deque.removeFirst();
            if (node.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                return node;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                deque.addLast(node.getChild(i));
            }
        }
        return null;
    }



}
