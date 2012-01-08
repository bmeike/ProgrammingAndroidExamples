package com.oreilly.demo.android.pa.sensordemo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NFC233 extends Activity {
    private static enum NFCType {
        UNKNOWN, TEXT, URI, SMART_POSTER, ABSOLUTE_URI
    }

    private final Handler enableForegroundDispatchHandler = new Handler() {
        public void handleMessage(Message msg) {
            enableForegroundDispatch();
        }
    };

    private final Handler enableForegroundPushHandler = new Handler() {
        public void handleMessage(Message msg) {
            enableForegroundPush();
        }
    };

    private final Handler writeTagHandler = new Handler() {
        public void handleMessage(Message msg) {
            writeTag();
        }
    };

    private final Handler mgsToaster = new Handler() {
        public void handleMessage(Message msg) {
            toastMessage(msg.obj);
        }
    };

    private Tag mytag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc233);

        setTitle("Near Field Communication - 2.3.3");

        setupView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(NfcAdapter.getDefaultAdapter(this) != null) {
                NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
                NfcAdapter.getDefaultAdapter(this).disableForegroundNdefPush(this);
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
    }

    private void setupView() {
        findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // if what launched this was caused by ACTION_NDEF_DISCOVERED or
        // ACTION_TECH_DISCOVERED then get rid of the buttons and analyze the
        // intent.
        try {
            if(getIntent() != null && getIntent().getAction() != null &&
                (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ||
                    getIntent().getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED))) {

                findViewById(R.id.enablefdispatch).setVisibility(View.GONE);
                findViewById(R.id.enablefpush).setVisibility(View.GONE);

                findViewById(R.id.tagtype).setVisibility(View.VISIBLE);
                findViewById(R.id.tagid).setVisibility(View.VISIBLE);
                findViewById(R.id.tagdata).setVisibility(View.VISIBLE);
                findViewById(R.id.what).setVisibility(View.VISIBLE);
                findViewById(R.id.tagwrite).setVisibility(View.VISIBLE);

                mytag = getTag(getIntent());
                analyzeIntent(getIntent());
            } else if(NfcAdapter.getDefaultAdapter(this) == null || !NfcAdapter.getDefaultAdapter(this).isEnabled()) {
                findViewById(R.id.enablefdispatch).setVisibility(View.GONE);
                findViewById(R.id.enablefpush).setVisibility(View.GONE);
                findViewById(R.id.tagtype).setVisibility(View.GONE);
                findViewById(R.id.tagdata).setVisibility(View.GONE);

                ((TextView) findViewById(R.id.tagid)).setText("NFC not enabled!");
            } else {
                findViewById(R.id.tagtype).setVisibility(View.GONE);
                findViewById(R.id.tagid).setVisibility(View.GONE);
                findViewById(R.id.tagdata).setVisibility(View.GONE);

                findViewById(R.id.enablefdispatch).setVisibility(View.VISIBLE);
                findViewById(R.id.enablefdispatch).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableForegroundDispatchHandler.sendEmptyMessage(0);
                    }
                });

                findViewById(R.id.enablefpush).setVisibility(View.VISIBLE);
                findViewById(R.id.enablefpush).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableForegroundPushHandler.sendEmptyMessage(0);
                    }
                });
            }

            findViewById(R.id.tagwrite).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeTagHandler.sendEmptyMessage(0);
                }
            });
        } catch (Exception t) {
            ((TextView) findViewById(R.id.tagid)).setText("ERROR: "+t.toString());
        }
    }

    private void enableForegroundDispatch() {
        findViewById(R.id.enablefdispatch).setVisibility(View.GONE);
        Toast.makeText(getBaseContext(), "Foreground Dispatch Enabled! Please scan a tag", Toast.LENGTH_SHORT).show();

        PendingIntent intent = PendingIntent.getActivity(this, 0,
            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0);

        // we are going set up to receive as a ACTION_TAG_DISCOVERED intent
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, intent, null, null);
    }

    @Override
    public void onNewIntent(Intent intent) {
        findViewById(R.id.tagtype).setVisibility(View.VISIBLE);
        findViewById(R.id.tagid).setVisibility(View.VISIBLE);
        findViewById(R.id.tagdata).setVisibility(View.VISIBLE);
        findViewById(R.id.what).setVisibility(View.VISIBLE);
        findViewById(R.id.tagwrite).setVisibility(View.VISIBLE);

        mytag = getTag(intent);
        analyzeIntent(intent);
    }

    private void enableForegroundPush() {
        findViewById(R.id.enablefpush).setVisibility(View.GONE);
        Toast.makeText(getBaseContext(), "Foreground Push Enabled! Please tap another NFC endabled Android phone", Toast.LENGTH_SHORT).show();

        NdefRecord[] rec = new NdefRecord[1];
        rec[0] = newTextRecord("NFC Foreground Push Message");
        NdefMessage msg = new NdefMessage(rec);

        NfcAdapter.getDefaultAdapter(this).enableForegroundNdefPush(this, msg);
    }

    private void writeTag() {
        if(mytag == null) {
            Toast.makeText(this, "No tag available to write to!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Ndef ndefref = Ndef.get(mytag);

        if(ndefref == null) {
            Toast.makeText(this, "Tag is not Ndef: NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!ndefref.isWritable()) {
            Toast.makeText(this, "The tag is not writable!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText what = (EditText) findViewById(R.id.what);
        if(what == null || what.getText() == null || what.getText().toString() == null || what.getText().toString().trim().length() < 1) {
            Toast.makeText(this, "Please input some text to write to the tag.", Toast.LENGTH_SHORT).show();
            return;
        }

        String msgstr = what.getText().toString().trim();

        NdefRecord[] rec = new NdefRecord[1];
        rec[0] = newTextRecord(msgstr);
        final NdefMessage msg = new NdefMessage(rec);

        (new Thread() {
            public void run() {
                try {
                    Message.obtain(mgsToaster, 0, "Tag writing attempt started").sendToTarget();

                    int count = 0;
                    if(!ndefref.isConnected()) {
                        ndefref.connect();
                    }
                    while(!ndefref.isConnected()) {
                        if(count > 6000) {
                            throw new Exception("Unable to connect to tag");
                        }
                        count++;
                        sleep(10);
                    }
                    ndefref.writeNdefMessage(msg);

                    Message.obtain(mgsToaster, 0, "Tag write successful!").sendToTarget();
                } catch (Exception t) {
                    t.printStackTrace();

                    Message.obtain(mgsToaster, 0, "Tag writing failed! - "+t.getMessage()).sendToTarget();
                } finally {
                    // ignore close failure...
                    try { ndefref.close(); }
                    catch (IOException e) { }
                }
            }
        }).start();
    }

    private NdefRecord newTextRecord(String text) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));

        byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));

        char status = (char) (langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    private Tag getTag(final Intent intent) {
        if(intent == null || !intent.hasExtra(NfcAdapter.EXTRA_TAG)) return null;

        Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag == null) return null;
        return (Tag) tag;
    }

    private void toastMessage(Object ob) {
        String msg = "";
        if(ob == null || !(ob instanceof String)) {
            msg = "-- Attempt to toast msg failed --";
        } else {
            msg = (String) ob;
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // From here on down is the same code as in com.oreilly.demo.android.pa.sensordemo.NFC
    private void analyzeIntent(final Intent intent) {
        if(intent == null) return;

        String id = getTagId(intent);
        NFCType type = NFCType.UNKNOWN;
        String datastr = null;
        byte[] data = null;

        NdefMessage tag = getTagData(intent);
        if(tag != null) {
            type = getTagType(tag);
            if(type != NFCType.UNKNOWN) {
                datastr = getTagData(tag);
            } else data = getTagRawData(tag);
        }

        if(datastr != null) updateViewInfo(id, type, datastr);
        else updateViewInfo(id, type, data);
    }

    private void updateViewInfo(String id, NFCType type, byte[] data) {
        updateViewInfo(id, type, data != null ? getHexString(data) : null);
    }

    private void updateViewInfo(String id, NFCType type, String data) {
        if(id != null) {
            ((TextView) findViewById(R.id.tagid)).setText("TagID: "+id);
        }

        if(type != NFCType.UNKNOWN) {
            String typestr = "";
            switch(type) {
                case TEXT: typestr = "Text"; break;
                case SMART_POSTER: typestr = "Smart Poster"; break;
                case URI: typestr = "URI"; break;
                case ABSOLUTE_URI: typestr = "URI (Abs)"; break;
                default: typestr = "UNKNOWN";
                break;
            }
            ((TextView) findViewById(R.id.tagtype)).setText("TagType: "+typestr);
        }

        if(data != null) {
            ((TextView) findViewById(R.id.tagdata)).setText("TagData:\n"+data);
        }
    }

    private String getTagId(final Intent intent) {
        if(intent == null) return null;
        byte[] byte_id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        if(byte_id == null) return null;

        return getHexString(byte_id);
    }

    private NdefMessage getTagData(final Intent intent) {
        if(intent == null || !intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) return null;

        Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if(msgs == null || msgs.length < 1) {
            return null;
        }

        NdefMessage[] nmsgs = new NdefMessage[msgs.length];
        for(int i=0;i<msgs.length;i++) {
            nmsgs[i] = (NdefMessage) msgs[i];
        }

        // we will only grab the first msg as we are handling only 1 tag at the moment
        return nmsgs[0];
    }

    private NFCType getTagType(final NdefMessage msg) {
        if(msg == null) return null;
        // we are only grabbing the first recognizable item

        for (NdefRecord record : msg.getRecords()) {
            if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                    return NFCType.TEXT;
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                    return NFCType.URI;
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)) {
                    return NFCType.SMART_POSTER;
                }
            } else if(record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI) {
                return NFCType.ABSOLUTE_URI;
            }
        }
        return null;
    }

    private String getTagData(final NdefMessage msg) {
        if(msg == null) return null;
        // we are only grabbing the first recognizable item

        for(NdefRecord record : msg.getRecords()) {
            if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                    return getText(record.getPayload());
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                    return getURI(record.getPayload());
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)) {
                    if(record.getPayload() == null || record.getPayload().length < 1) return null;
                    try {
                        NdefMessage subrecords = new NdefMessage(record.getPayload());
                        return getSubRecordData(subrecords.getRecords());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } else if(record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI) {
                return getAbsoluteURI(record.getPayload());
            }
        }
        return null;
    }

    private String getSubRecordData(final NdefRecord[] records) {
        if(records == null || records.length < 1) return null;
        String data = "";
        for(NdefRecord record : records) {
            if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                    data += getText(record.getPayload()) + "\n";
                }
                if(Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                    data += getURI(record.getPayload()) + "\n";
                } else {
                    data += "OTHER KNOWN DATA\n";
                }
            } else if(record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI) {
                data += getAbsoluteURI(record.getPayload()) + "\n";
            } else data += "OTHER UNKNOW DATA\n";
        }
        return data;
    }

    private byte[] getTagRawData(final NdefMessage msg) {
        if(msg == null || msg.getRecords().length < 1) return null;
        // we are only grabbing the first item
        return msg.getRecords()[0].getPayload();
    }

    /*
     * the First Byte of the payload contains the "Status Byte Encodings" field, per the NFC Forum "Text Record Type Definition" section 3.2.1.
     *
     * Bit_7 is the Text Encoding Field.
     * * if Bit_7 == 0 the the text is encoded in UTF-8 else if Bit_7 == 1 then the text is encoded in UTF16
     * Bit_6 is currently always 0 (reserved for future use)
     * Bits 5 to 0 are the length of the IANA language code.
     */
    private String getText(final byte[] payload) {
        if(payload == null) return null;
        try {
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAbsoluteURI(final byte[] payload) {
        if(payload == null) return null;
        return new String(payload, Charset.forName("UTF-8"));
    }

    /**
     * the First Byte of the payload contains the prefix byte
     */
    private String getURI(final byte[] payload) {
        if(payload == null || payload.length < 1) return null;
        String prefix = convertUriPrefix(payload[0]);
        return (prefix != null ? prefix  : "" ) + new String(Arrays.copyOfRange(payload, 1,payload.length));
    }

    /**
     * NFC Forum "URI Record Type Definition"
     *
     * Conversion of prefix based on section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
    private String convertUriPrefix(final byte prefix) {
        if(prefix == (byte) 0x00) return "";
        else if(prefix == (byte) 0x01) return "http://www.";
        else if(prefix == (byte) 0x02) return "https://www.";
        else if(prefix == (byte) 0x03) return "http://";
        else if(prefix == (byte) 0x04) return "https://";
        else if(prefix == (byte) 0x05) return "tel:";
        else if(prefix == (byte) 0x06) return "mailto:";
        else if(prefix == (byte) 0x07) return "ftp://anonymous:anonymous@";
        else if(prefix == (byte) 0x08) return "ftp://ftp.";
        else if(prefix == (byte) 0x09) return "ftps://";
        else if(prefix == (byte) 0x0A) return "sftp://";
        else if(prefix == (byte) 0x0B) return "smb://";
        else if(prefix == (byte) 0x0C) return "nfs://";
        else if(prefix == (byte) 0x0D) return "ftp://";
        else if(prefix == (byte) 0x0E) return "dav://";
        else if(prefix == (byte) 0x0F) return "news:";
        else if(prefix == (byte) 0x10) return "telnet://";
        else if(prefix == (byte) 0x11) return "imap:";
        else if(prefix == (byte) 0x12) return "rtsp://";
        else if(prefix == (byte) 0x13) return "urn:";
        else if(prefix == (byte) 0x14) return "pop:";
        else if(prefix == (byte) 0x15) return "sip:";
        else if(prefix == (byte) 0x16) return "sips:";
        else if(prefix == (byte) 0x17) return "tftp:";
        else if(prefix == (byte) 0x18) return "btspp://";
        else if(prefix == (byte) 0x19) return "btl2cap://";
        else if(prefix == (byte) 0x1A) return "btgoep://";
        else if(prefix == (byte) 0x1B) return "tcpobex://";
        else if(prefix == (byte) 0x1C) return "irdaobex://";
        else if(prefix == (byte) 0x1D) return "file://";
        else if(prefix == (byte) 0x1E) return "urn:epc:id:";
        else if(prefix == (byte) 0x1F) return "urn:epc:tag:";
        else if(prefix == (byte) 0x20) return "urn:epc:pat:";
        else if(prefix == (byte) 0x21) return "urn:epc:raw:";
        else if(prefix == (byte) 0x22) return "urn:epc:";
        else if(prefix == (byte) 0x23) return "urn:nfc:";
        return null;
    }


    private final static char[] HEX = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    // convert bytes to a hex string
    private static String getHexString(final byte[] bytes) {
        StringBuffer hex = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 1; j >= 0; j--) {
                hex.append(HEX[(bytes[i] >> (j * 4)) & 0xF]);
            }
        }
        return hex.toString();
    }
}
