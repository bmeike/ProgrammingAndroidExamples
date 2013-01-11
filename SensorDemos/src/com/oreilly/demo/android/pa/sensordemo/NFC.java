package com.oreilly.demo.android.pa.sensordemo;

import java.nio.charset.Charset;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NFC extends Activity {
    private static enum NFCType {
        UNKNOWN, TEXT, URI, SMART_POSTER, ABSOLUTE_URI
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc);

        setTitle("Near Field Communication");

        findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        analyzeIntent(getIntent());
    }

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
