package com.example.paul.nfc2;

/**
 * Created by Paul on 11/19/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class ReadTagActivity extends Activity {

    private static final String TAG = ReadTagActivity.class.getSimpleName();

    // NFC-related variables
    private NfcAdapter _nfcAdapter;
    private PendingIntent _nfcPendingIntent;
    IntentFilter[] _readTagFilters;

    private TextView _textViewData;
    private TextView part1;
    private TextView part2;

    WifiManager wifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        //wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        _textViewData = (TextView) findViewById(R.id.textData);

        _nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (_nfcAdapter == null) {
            Toast.makeText(this, "Your device does not support NFC. Cannot run this app.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //checkNfcEnabled();

        _nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("application/com.paul.sample.nfc2");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Could not add MIME type.", e);
        }

        _readTagFilters = new IntentFilter[]{ndefDetected};

        part1 = (TextView) findViewById(R.id.textView);
        part2 = (TextView) findViewById(R.id.textView2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //checkNfcEnabled();

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                NdefMessage[] msgs = getNdefMessagesFromIntent(getIntent());
                NdefRecord record = msgs[0].getRecords()[0];
                byte[] payload = record.getPayload();

                String payloadString = new String(payload);

                _textViewData.setText(payloadString);
            }
        }

        _nfcAdapter.enableForegroundDispatch(this, _nfcPendingIntent, _readTagFilters, null);

    }

    @Override
    protected void onPause() {
        super.onPause();

        _nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
            confirmDisplayedContentOverwrite(msgs[0]);

        } else if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Toast.makeText(this, "This NFC tag has no NDEF data.", Toast.LENGTH_LONG).show();
        }
    }

    NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }

        } else {
            Log.e(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void confirmDisplayedContentOverwrite(final NdefMessage msg) {
        final String data = _textViewData.getText().toString().trim();

        new AlertDialog.Builder(this).setTitle("NFC tag found!").setMessage("Do you want to connect to the wifi?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String payload = new String(msg.getRecords()[0].getPayload());
                        _textViewData.setText(new String(payload));

                        String[] parts = payload.split("-");
                        String SSID = parts[0];
                        String Password = parts[1];
                        connectToWifi(SSID, Password);


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                     public void onClick(DialogInterface dialog, int id) {
                         _textViewData.setText(data);
                        dialog.cancel();
                    }
        }).show();

    }


    private void connectToWifi(final String networkSSID, final String networkPassword){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\""+ networkPassword +"\"";

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        //if (!wifiManager.isWifiEnabled()){
        //    wifiManager.setWifiEnabled(true);
        //}
        wifiManager.addNetwork(conf);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}