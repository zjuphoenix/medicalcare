package com.zju.medicalcare.disruptor;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

import com.lmax.disruptor.EventHandler;
import com.zju.medicalcare.command.CommandResult;
import com.zju.medicalcare.description.ModuleDescriptionInfo;
import com.zju.medicalcare.model.BloodKetone;
import com.zju.medicalcare.model.BloodOxygen;
import com.zju.medicalcare.model.BloodPressure;
import com.zju.medicalcare.model.BloodSugar;
import com.zju.medicalcare.model.ECG;
import com.zju.medicalcare.model.ProtocolDescription;
import com.zju.medicalcare.modelstate.BloodKetoneState;
import com.zju.medicalcare.modelstate.BloodOxygenState;
import com.zju.medicalcare.modelstate.BloodPressureState;
import com.zju.medicalcare.modelstate.BloodSugarState;
import com.zju.medicalcare.modelstate.ECGState;
import com.zju.medicalcare.util.FileUtil;

public class DataFrameEventHandler implements EventHandler<DataFrameEvent> {

	private ProtocolDescription protocolDescription = new ProtocolDescription();
	private ModuleDescriptionInfo info = new ModuleDescriptionInfo();
	private ECG ecg = new ECG();
	private ECGState ecgState = new ECGState();
	private CommandResult commandResult = new CommandResult();
	private BloodOxygen bloodOxygen = new BloodOxygen();
	private BloodOxygenState bloodOxygenState = new BloodOxygenState();
	private BloodPressure bloodPressure = new BloodPressure();
	private BloodPressureState bloodPressureState = new BloodPressureState();
	private BloodSugar bloodSugar = new BloodSugar();
	private BloodSugarState bloodSugarState = new BloodSugarState();
	private BloodKetone bloodKetone = new BloodKetone();
	private BloodKetoneState bloodKetoneState = new BloodKetoneState();
	
	private int count = 0;
		
	@SuppressWarnings("resource")
	@Override
	public void onEvent(DataFrameEvent dataFrameEvent, long sequence,
			boolean endOfBatch) throws Exception {
		byte[] data;
		int modelType;
		@SuppressWarnings("unused")
		int length = 0;
		switch (dataFrameEvent.getType()) {
		case 0:// 协议描述
			data = dataFrameEvent.getData();
			System.out.println("协议描述         "+"长度:"+dataFrameEvent.getLength());
			protocolDescription.result = data[0];// 结果
			protocolDescription.code = data[1];// 编码方式
			protocolDescription.version = data[2];// 协议版本
			protocolDescription.productID = data[3];// 产品号
			protocolDescription.deviceID = data[4];// 设备号
			protocolDescription.reservedbit = (char) ((data[5]&0xff << 8) + (data[6]&0xff));// 保留位
			
			if(dataFrameEvent.getLength()!=7){
				System.out.println("协议描述异常");
			}
			break;
		case 1:// 心电模块
			data = dataFrameEvent.getData();
			modelType = data[0]&0xff;
			length = (data[1]&0xff << 8) + (data[2]&0xff);
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) ((data[7]&0xff << 8) + (data[8]&0xff));// 保留位
				System.out.println("心电模块描述         "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 1) {// 模块数据
				ecg.breathingrate = (data[3]&0xff << 8) + (data[4]&0xff);
				ecg.ST1 = (data[5]&0xff << 8) + (data[6]&0xff);
				ecg.ST2 = (data[7]&0xff << 8) + (data[8]&0xff);
				ecg.ST3 = (data[9]&0xff << 8) + (data[10]&0xff);
				ecg.heartrate = (data[11]&0xff << 8) + (data[12]&0xff);
				int k = 0;
				int i = 13;
				while (k < 500) {
					ecg.ecg1[k++] = (char) ((data[i++]&0xff << 8) + (data[i++]&0xff));
				}
				k = 0;
				while (k < 500) {
					ecg.ecg2[k++] = (char) ((data[i++]&0xff << 8) + (data[i++]&0xff));
				}
				k = 0;
				while (k < 500) {
					ecg.ecg3[k++] = (char) ((data[i++]&0xff << 8) + (data[i++]&0xff));
				}
				k = 0;
				while (k<500) {
					ecg.flag[k++] = data[i++];
				}
				File file = new File(this.getClass().getResource("/")+"ecg/"+(count++));
				file.createNewFile();
		        RandomAccessFile raf = new RandomAccessFile(file, "rw");
		        FileChannel fileChannel = raf.getChannel();
		        MappedByteBuffer mbb = fileChannel.map(MapMode.READ_WRITE, 0, 3000);
		        mbb.put(data, 13, 3000);
		        FileUtil.unmap(mbb);
		        fileChannel.close();
				System.out.println("心电模块数据        "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 2) {// 模块状态
				ecgState.wirestate = data[3];
				ecgState.overloadinfo = data[4];
				System.out.println("心电模块状态          "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) ((data[4]&0xff << 8) + (data[5]&0xff));// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
				System.out.println("心电模块命令    "+"长度:"+dataFrameEvent.getLength());
			}
			break;
		case 2:// 血糖模块
			data = dataFrameEvent.getData();
			modelType = data[0]&0xff;
			length = (data[1]&0xff << 8) + (data[2]&0xff);
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) ((data[7]&0xff << 8) + (data[8]&0xff));// 保留位
				System.out.println("血糖模块描述    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 1) {// 模块数据
				bloodSugar.bloodsugar = (data[3]&0xff << 24) | (data[4]&0xff << 16)
						| (data[5]&0xff << 8) | data[6]&0xff;
				System.out.println("血糖模块数据    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 2) {// 模块状态
				bloodSugarState.state = data[3];
				System.out.println("血糖模块状态    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) ((data[4]&0xff << 8) | (data[5]&0xff));// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
				System.out.println("血糖模块命令     "+"长度:"+dataFrameEvent.getLength());
			}

			break;
		case 3:// 血压模块
			data = dataFrameEvent.getData();
			modelType = data[0]&0xff;
			length = (data[1]&0xff << 8) + (data[2]&0xff);
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) ((data[7]&0xff << 8) + (data[8]&0xff));// 保留位
				System.out.println("血压模块描述    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 1) {// 模块数据
				bloodPressure.systolicpressure = (data[3]&0xff << 8) + (data[4]&0xff);
				bloodPressure.diastolicpressure = (data[5]&0xff << 8) + (data[6]&0xff);
				bloodPressure.meanpressure = (data[7]&0xff << 8) + (data[8]&0xff);
				bloodPressure.pulserate = (data[9]&0xff << 8) + (data[10]&0xff);
				System.out.println("血压模块数据    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 2) {// 模块状态
				bloodPressureState.cuffpressure = (char) ((data[3]&0xff << 8) + (data[4]&0xff));
				bloodPressureState.state = data[5];
				bloodPressureState.type = data[6];
				bloodPressureState.error = data[7];
				System.out.println("血压模块状态    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) ((data[4]&0xff << 8) + (data[5]&0xff));// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
				System.out.println("血压模块命令    "+"长度:"+dataFrameEvent.getLength());
			}
			break;
		case 4:// 血氧模块
			data = dataFrameEvent.getData();
			modelType = data[0]&0xff;
			length = (data[1]&0xff << 8) + (data[2]&0xff);
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) ((data[7]&0xff << 8) + (data[8]&0xff));// 保留位
				System.out.println("血氧模块描述    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 1) {// 模块数据
				bloodOxygen.pulserate = (data[3]&0xff << 8) + (data[4]&0xff);
				bloodOxygen.saturation = data[5];
				bloodOxygen.pulseintensity = data[6];
				System.arraycopy(data, 7, bloodOxygen.waveform, 0, 125);
				System.arraycopy(data, 132, bloodOxygen.oxygenmark, 0, 125);
				System.out.println("血氧模块数据    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 2) {// 模块状态	
				bloodOxygenState.state = data[3];
				System.out.println("血氧模块状态    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) ((data[4]&0xff << 8) + (data[5]&0xff));// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
				System.out.println("血氧模块命令    "+"长度:"+dataFrameEvent.getLength());
			}
			break;
		case 5:// 血酮模块
			data = dataFrameEvent.getData();
			modelType = data[0]&0xff;
			length = (data[1]&0xff << 8) | (data[2]&0xff);
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) ((data[7]&0xff << 8) | (data[8]&0xff));// 保留位
				System.out.println("血酮模块描述    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 1) {// 模块数据
				bloodKetone.bloodketone = (data[3]&0xff << 24) | (data[4]&0xff << 16)
						| (data[5]&0xff << 8) | (data[6]&0xff);
				System.out.println("血酮模块数据    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 2) {// 模块状态
				bloodKetoneState.state = data[3];
				System.out.println("血酮模块状态    "+"长度:"+dataFrameEvent.getLength());
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) ((data[4]&0xff << 8) | (data[5]&0xff));// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
				System.out.println("血酮模块命令     "+"长度:"+dataFrameEvent.getLength());
			}
			break;
		default:
			System.out.println("数据帧类型异常");
			break;
		}
		dataFrameEvent.setPos(0);
	}

}
