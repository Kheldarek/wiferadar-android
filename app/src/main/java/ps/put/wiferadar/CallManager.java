package ps.put.wiferadar;

import android.net.Uri;
import android.telecom.Call;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class CallManager {
    private static final String LOG_TAG = "CallManager";

    private static BehaviorSubject<GsmCall> subject = BehaviorSubject.create();
    private static Call currentCall = null;

    public static Observable<GsmCall> updates() {
        return subject;
    }

    public static void updateCall(Call call) {
        currentCall = call;
        if (call != null) {
            subject.onNext(toCallStatus(call));
        }
    }

    public static void cancelCall() {
        if (currentCall != null) {
            if (currentCall.getState() == Call.STATE_RINGING) {
                rejectCall();
            } else {
                disconnectCall();
            }
        }
    }

    public static void acceptCall() {
        Log.i(LOG_TAG, "acceptCall");
        if (currentCall != null) {
            currentCall.answer(currentCall.getDetails().getVideoState());
        }
    }

    private static void rejectCall() {
        Log.i(LOG_TAG, "reject call");
        if (currentCall != null) {
            currentCall.reject(false, "");
        }
    }

    private static void disconnectCall() {
        Log.i(LOG_TAG, "disconnect call");
        if (currentCall != null) {
            currentCall.disconnect();
        }
    }

    private static GsmCall toCallStatus(Call call) {
        Uri handle = call.getDetails().getHandle();
        Log.i(LOG_TAG, handle.toString());
        if (call.getState() == Call.STATE_ACTIVE) {
            return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.ACTIVE);
        }
        if (call.getState() == Call.STATE_RINGING) {
            return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.RINGING);
        }
        if (call.getState() == Call.STATE_CONNECTING) {
            return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.CONNECTING);
        }
        if (call.getState() == Call.STATE_DIALING) {
            return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.DIALING);
        }
        if (call.getState() == Call.STATE_DISCONNECTED) {
            return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.DISCONNECTED);
        }
        return new GsmCall(handle.getSchemeSpecificPart(), GsmCall.Status.UNKNOWN);
    }


}
