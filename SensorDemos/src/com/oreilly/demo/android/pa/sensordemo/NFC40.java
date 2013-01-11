package com.oreilly.demo.android.pa.sensordemo;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NFC40 extends Activity {
	private static final boolean AARUSE					= false;
	
	private CreateNdefMessageCallback nfccallback = new CreateNdefMessageCallback() {
		@Override
		public NdefMessage createNdefMessage(NfcEvent event) {
			 return createNDFMsg(false);
		}
	};
	
	private NdefMessage createNDFMsg(boolean now) {
		String text = "Beam "+(now ? "NOW" : "ON")+" com.oreilly.demo.android.pa.sensordemo  Beam with" + (AARUSE ? "" : "out") + " AAR Use";
		 byte[] mimeBytes =  "application/com.oreilly.demo.android.pa.sensordemo".getBytes(Charset.forName("US-ASCII"));
		 NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], text.getBytes());
		 
		 NdefMessage msg = new NdefMessage(new NdefRecord[] {mimeRecord});
		 
		 NdefMessage msgWaAAR = new NdefMessage(new NdefRecord[] {mimeRecord,
				 NdefRecord.createApplicationRecord("com.oreilly.demo.android.pa.sensordemo")});		
		 
		return AARUSE ? msgWaAAR : msg;
	}
	 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc40);

        setTitle("Near Field Communication - 4.0");
    }
	
	@Override
	public void onResume() {
		super.onResume();
		setupView();
	}
	
	@Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }
	
	private void doBeamOn() {
		NfcAdapter nfcadapter = NfcAdapter.getDefaultAdapter(this);
		if(nfcadapter != null) {
			nfcadapter.setNdefPushMessageCallback(nfccallback, this);
		}
	}
	
	private void doBeamNow() {
		NfcAdapter nfcadapter = NfcAdapter.getDefaultAdapter(this);
		if(nfcadapter != null) {
			nfcadapter.setNdefPushMessage(createNDFMsg(true), this);
		}
	}
	
	private void setupView() {
		findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        try {
            if(getIntent() != null && getIntent().getAction() != null &&
                (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))) {
            	findViewById(R.id.beamon).setVisibility(View.GONE);
                findViewById(R.id.beamnow).setVisibility(View.GONE);

                findViewById(R.id.tagdata).setVisibility(View.VISIBLE);

                analyzeIntent(getIntent());
            } else if(NfcAdapter.getDefaultAdapter(this) == null || !NfcAdapter.getDefaultAdapter(this).isEnabled()) {
                findViewById(R.id.beamon).setVisibility(View.GONE);
                findViewById(R.id.beamnow).setVisibility(View.GONE);

                ((TextView) findViewById(R.id.tagdata)).setText("NFC not enabled!");
            } else {
                findViewById(R.id.tagdata).setVisibility(View.GONE);

                findViewById(R.id.beamon).setVisibility(View.VISIBLE);
                findViewById(R.id.beamon).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	doBeamOn();
                    }
                });
                
                findViewById(R.id.beamnow).setVisibility(View.VISIBLE);
                findViewById(R.id.beamnow).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	doBeamNow();
                    }
                });
            } 
    	} catch (Exception t) {
    		findViewById(R.id.tagdata).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tagdata)).setText("ERROR: "+t.toString());
            t.printStackTrace();
        }
	}
    
	private void analyzeIntent(final Intent intent) {
        if(intent == null) return;
        findViewById(R.id.tagdata).setVisibility(View.VISIBLE);
        
        Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        ((TextView) findViewById(R.id.tagdata)).setText(new String(((NdefMessage) msgs[0]).getRecords()[0].getPayload()));
    }
}
