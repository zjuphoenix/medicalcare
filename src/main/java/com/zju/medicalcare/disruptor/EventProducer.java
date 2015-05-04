package com.zju.medicalcare.disruptor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.lmax.disruptor.RingBuffer;

public class EventProducer implements Runnable {

	private BufferedInputStream bis;
	private final RingBuffer<DataFrameEvent> ringBuffer;
	private final static int STATE_READHEAD = 0;
	private final static int STATE_READDATA = 1;
	private volatile boolean isFinished = false;

	public EventProducer(BufferedInputStream bis,
			RingBuffer<DataFrameEvent> ringBuffer) {
		this.bis = bis;
		this.ringBuffer = ringBuffer;
	}

	public void stopTask() {
		isFinished = true;
	}

	/*@Override
	public void run() {
		byte[] buf = new byte[8 * 1024];
		byte[] head = new byte[3];
		int pos = 0;
		int length = 0;
		int b = 0;
		int num = 0;
		try {
			FileOutputStream fos = new FileOutputStream("E://Study/garea.txt");
			while(true){
				if(num>10){
					bis.close();
					break;
				}
				try {
					while((b = bis.read(buf,0,buf.length))==-1);
					num++;
					//fos.write(buf, 0, b);
					for (int i = 0; i < b; i++) {
						fos.write(buf[i]&0xff);
						//System.out.print(buf[i]&0xff);
					}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			fos.close();
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		
		
	}*/

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		byte[] buf = new byte[8 * 1024];
		byte[] head = new byte[3];
		int head_pos = 0;
		int buf_pos = 0;
		int b = 0;
		DataFrameEvent dataFrameEvent = null;
		int type;
		int length;
		int dataleft = 0;
		int state = 0;
		long sequence = 0;
		try {
			while (!isFinished) {
				if (buf_pos == b) {// buf被读完
					while(!isFinished){
						if((b = bis.read(buf,0,buf.length))!=-1){
							break;
						}
					}
					if (isFinished) {
						break;
					}
					buf_pos = 0;
				}
				switch (state) {
				case STATE_READHEAD:
					if(!(buf_pos < b && head_pos < 3)){
						System.out.println("数据长度异常");
					}
					while (buf_pos < b && head_pos < 3) {
						head[head_pos++] = buf[buf_pos++];
					}
					if (head_pos == 3) {
						state = STATE_READDATA;
						type = head[0]&0xff;
						System.out.println("type:"+type);
						//int head1 = ((int)head[1]) << 8;
						//int head2 = (int)head[2];
						length = ((head[2]&0xff) << 8) + (head[1]&0xff);
						//length = head1 + head2;
						head_pos = 0;
						sequence = ringBuffer.next();
						dataFrameEvent = ringBuffer.get(sequence);
						dataFrameEvent.setType(type);
						dataFrameEvent.setLength(length);
						
						dataleft = length;
						if(dataleft>3513 || dataleft<0){
							System.out.println("数据长度异常");
						}
					}
					break;
				case STATE_READDATA:
					// 缓冲区包含数据帧剩下的所有数据
					if (b - buf_pos >= dataleft) {
						dataFrameEvent.copyData(buf, buf_pos, dataleft);
						buf_pos += dataleft;
						state = STATE_READHEAD;
						head_pos = 0;
						ringBuffer.publish(sequence);
					} else {// 缓冲区包含数据帧剩下数据的一部分
						dataFrameEvent.copyData(buf, buf_pos, b - buf_pos);
						dataleft -= (b - buf_pos);
						if(dataleft!=dataFrameEvent.remainDataLength()){
							System.out.println("数据长度异常");
						}
						buf_pos += (b - buf_pos);
					}
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
