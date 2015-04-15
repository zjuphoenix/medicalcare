package com.zju.medicalcare.disruptor;

import java.io.BufferedInputStream;
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
					while (!isFinished
							&& (b = bis.read(buf, 0, buf.length)) != -1)
						;
					if (isFinished) {
						break;
					}
					buf_pos = 0;
				}
				switch (state) {
				case STATE_READHEAD:
					while (buf_pos < b && head_pos < 3) {
						head[head_pos++] = buf[buf_pos++];
					}
					if (head_pos == 3) {
						state = STATE_READDATA;
						type = head[0];
						length = head[1] << 8 | head[2];
						head_pos = 0;
						sequence = ringBuffer.next();
						dataFrameEvent = ringBuffer.get(sequence);
						dataFrameEvent.setType(type);
						dataFrameEvent.setLength(length);
						
						dataleft = length;
					}
					break;
				case STATE_READDATA:
					// 缓冲区包含数据帧剩下的所有数据
					if (b - buf_pos >= dataleft) {
						dataFrameEvent.copyData(buf, buf_pos, dataleft);
						buf_pos += dataleft;
						state = STATE_READHEAD;
						ringBuffer.publish(sequence);
					} else {// 缓冲区包含数据帧剩下数据的一部分
						dataFrameEvent.copyData(buf, buf_pos, b - buf_pos);
						dataleft -= (b - buf_pos);
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
