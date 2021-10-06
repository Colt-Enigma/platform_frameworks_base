/**
 * Copyright (C) 2019-2020 The LineageOS Project
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

package com.android.server.biometrics.sensors.fingerprint;

import android.os.Handler;
import android.os.Looper;
import android.util.Slog;
import android.os.RemoteException;

import vendor.lineage.biometrics.fingerprint.inscreen.V1_0.IFingerprintInscreen;
import vendor.lineage.biometrics.fingerprint.inscreen.V1_0.IFingerprintInscreenCallback;

import java.util.NoSuchElementException;


public class FODHelper {

    private static IFingerprintInscreen mFingerprintInscreenDaemon;

    public static String TAG = "FODHelper";

    public static IFingerprintInscreen getFingerprintInScreenDaemon() {
        if (mFingerprintInscreenDaemon == null) {
            try {
                mFingerprintInscreenDaemon = IFingerprintInscreen.getService();
                if (mFingerprintInscreenDaemon != null) {
                    mFingerprintInscreenDaemon.asBinder().linkToDeath((cookie) -> {
                        mFingerprintInscreenDaemon = null;
                    }, 0);
                }
            } catch (NoSuchElementException | RemoteException e) {
                // do nothing
            }
        }
        return mFingerprintInscreenDaemon;
    }

    public static void onError(int error, int vendorCode) {
        IFingerprintInscreen daemon = getFingerprintInScreenDaemon();
        if (daemon != null) {
            try {
                if (daemon.handleError(error, vendorCode)) {
                    return;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "handleError failed", e);
            }
        }
    }

    public static void onAcquired(int acquiredInfo, int vendorCode) {
        IFingerprintInscreen daemon = getFingerprintInScreenDaemon();
        if (daemon != null) {
            try {
                if (daemon.handleAcquired(acquiredInfo, vendorCode)) {
                    return;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "handleError failed", e);
            }
        }
    }

    public static void onStartEnroll() {
        IFingerprintInscreen fodDaemon = getFingerprintInScreenDaemon();
        if (fodDaemon != null) {
            try {
                fodDaemon.onStartEnroll();
            } catch (RemoteException e) {
                Slog.e(TAG, "onStartEnroll failed", e);
            }
        }
    }

    public static void onFinishEnroll() {
        IFingerprintInscreen fodDaemon = getFingerprintInScreenDaemon();
        if (fodDaemon != null) {
            try {
                fodDaemon.onFinishEnroll();
            } catch (RemoteException e) {
                Slog.e(TAG, "onStartEnroll failed", e);
            }
        }
    }
}
