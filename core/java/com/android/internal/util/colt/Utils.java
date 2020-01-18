/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.colt;

import android.app.ActivityManagerNative;
import android.app.UiModeManager;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.UserHandle;
import android.os.Vibrator;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.input.InputManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import com.android.internal.statusbar.IStatusBarService;
import android.util.TypedValue;

import com.android.internal.R;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static OverlayManager mOverlayService;

    // Method to detect whether an overlay is enabled or not
    public static boolean isThemeEnabled(String packageName) {
        mOverlayService = new OverlayManager();
        try {
            ArrayList<OverlayInfo> infos = new ArrayList<OverlayInfo>();
            infos.addAll(mOverlayService.getOverlayInfosForTarget("android",
                    UserHandle.myUserId()));
            infos.addAll(mOverlayService.getOverlayInfosForTarget("com.android.systemui",
                    UserHandle.myUserId()));
            for (int i = 0, size = infos.size(); i < size; i++) {
                if (infos.get(i).packageName.equals(packageName)) {
                    return infos.get(i).isEnabled();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class OverlayManager {
        private final IOverlayManager mService;

        public OverlayManager() {
            mService = IOverlayManager.Stub.asInterface(
                    ServiceManager.getService(Context.OVERLAY_SERVICE));
        }

        public void setEnabled(String pkg, boolean enabled, int userId)
                throws RemoteException {
            mService.setEnabled(pkg, enabled, userId);
        }

        public List<OverlayInfo> getOverlayInfosForTarget(String target, int userId)
                throws RemoteException {
            return mService.getOverlayInfosForTarget(target, userId);
        }
    }

    public static Bitmap scaleCenterInside(final Bitmap source, final int newWidth, final int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float widthRatio = (float) newWidth / sourceWidth;
        float heightRatio = (float) newHeight / sourceHeight;
        float ratio = Math.max(widthRatio, heightRatio);
        float scaledWidth = sourceWidth * ratio;
        float scaledHeight = sourceHeight * ratio;

        //Bitmap scaled = Bitmap.createScaledBitmap(source, (int)scaledWidth, (int)scaledHeight, true);

        RectF targetRect = null;
        if (newWidth > newHeight) {
            float inset = (scaledHeight - newHeight) / 2;
            targetRect = new RectF(0, -inset, newWidth, newHeight + inset);
        } else {
            float inset = (scaledWidth - newWidth) / 2;
            targetRect = new RectF(-inset, 0, newWidth + inset, newHeight);
        }
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        return dest;
    }
    
    // Method to detect whether the system dark theme is enabled or not
    public static boolean isDarkTheme(Context context) {
        UiModeManager mUiModeManager =
                context.getSystemService(UiModeManager.class);
        if (mUiModeManager == null) return false;
        int mode = mUiModeManager.getNightMode();
        return (mode == UiModeManager.MODE_NIGHT_YES);
    }

    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (android.R.attr.colorAccent, value, true);
        return value.data;
    }
}
