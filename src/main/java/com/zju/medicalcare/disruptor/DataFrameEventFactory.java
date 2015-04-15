package com.zju.medicalcare.disruptor;

import com.lmax.disruptor.EventFactory;

public class DataFrameEventFactory implements EventFactory<DataFrameEvent> {

	@Override
	public DataFrameEvent newInstance() {
		// TODO 自动生成的方法存根
		return new DataFrameEvent();
	}

}
