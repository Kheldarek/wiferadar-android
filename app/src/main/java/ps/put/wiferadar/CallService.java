package ps.put.wiferadar;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

public class CallService extends InCallService {
    public static final String LOG_TAG = "CallService";

    private Call.Callback callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Log.i(LOG_TAG, "Call.Callback onStateChanged: $call, state: $state");
            CallManager.updateCall(call);
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.i(LOG_TAG, "onCallAdded");
        call.registerCallback(callCallback);
        startActivity(new Intent(this, CallActivity.class));
        CallManager.updateCall(call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.i(LOG_TAG, "onCallRemoved: $call");
        call.unregisterCallback(callCallback);
        CallManager.updateCall(null);
    }
}
