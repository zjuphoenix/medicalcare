package com.zju.medicalcare.disruptor;

public class DataFrameEvent {
	private int type;
	private int length;
	private int pos;
	private byte[] data;
	public DataFrameEvent() {
		pos = 0;
		data = new byte[3513];
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}

	public byte[] getData() {
		return data;
	}

	public void copyData(byte[] buf,int off,int len){
		System.arraycopy(buf, off, data, pos, len);
		pos+=len;
	}
	
	public int remainDataLength(){
		return length - pos;
	}
}
