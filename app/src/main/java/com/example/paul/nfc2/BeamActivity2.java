package com.example.paul.nfc2;


import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;

/**
 * Created by Paul on 11/25/2017.
 */

public class BeamActivity2 extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private EditText _editTextData;

    NfcAdapter       _nfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam2);

        _editTextData = (EditText) findViewById(R.id.textData);

        _nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (_nfcAdapter == null)
        {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        _nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {

        String data = _editTextData.getText().toString().trim();

        String mimeType = "application/com.paul.sample.nfc";

        byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
        byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
        byte[] id = new byte[0];

        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, dataBytes);

        NdefMessage message = new NdefMessage(new NdefRecord[]{record});

        return message;
    }
}
