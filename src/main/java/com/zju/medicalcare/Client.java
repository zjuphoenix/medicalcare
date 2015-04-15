package com.zju.medicalcare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.zju.medicalcare.command.Command;
import com.zju.medicalcare.disruptor.DataFrameEvent;
import com.zju.medicalcare.disruptor.DataFrameEventFactory;
import com.zju.medicalcare.disruptor.DataFrameEventHandler;
import com.zju.medicalcare.disruptor.EventProducer;

public class Client {

	private Socket socket;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private Executor executor;
	private DataFrameEventFactory factory;
	private final static int BUFFER_SIZE = 1024;
	private Disruptor<DataFrameEvent> disruptor;
	private RingBuffer<DataFrameEvent> ringBuffer;

	@SuppressWarnings("unchecked")
	public Client() {
		try {
			socket = new Socket("localhost", 10001);
			bos = new BufferedOutputStream(socket.getOutputStream());
			bis = new BufferedInputStream(socket.getInputStream());
			executor = Executors.newSingleThreadExecutor();
			factory = new DataFrameEventFactory();
	        disruptor = new Disruptor<DataFrameEvent>(factory, BUFFER_SIZE, executor);
	        disruptor.handleEventsWith(new DataFrameEventHandler());
	        disruptor.start();
	        ringBuffer = disruptor.getRingBuffer();
		} catch (UnknownHostException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public void dataCollect() {
		EventProducer producer = new EventProducer(bis, ringBuffer);
        new Thread(producer).start();
	}

	public void startECG() throws IOException {
		bos.write(Command.STARTECG);
		bos.flush();
	}

	public void stopECG() throws IOException {
		bos.write(Command.STOPECG);
		bos.flush();
	}

}
