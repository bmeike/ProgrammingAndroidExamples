package com.finchframework.bluetooth;

import android.os.Handler;
import android.os.Message;

public class BtHelperHandler extends Handler {
	
	public enum MessageType {
		STATE,
		READ,
		WRITE,
		DEVICE,
		NOTIFY;
	}
    
    public Message obtainMessage(MessageType message, int count, Object obj) {
		return obtainMessage(message.ordinal(), count, -1, obj);
    	
    }
    
    public MessageType getMessageType(int ordinal) {
    	return MessageType.values()[ordinal];
    }

}
