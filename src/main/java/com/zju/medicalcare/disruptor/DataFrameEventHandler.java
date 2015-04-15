package com.zju.medicalcare.disruptor;

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
			protocolDescription.result = data[0];// 结果
			protocolDescription.code = data[1];// 编码方式
			protocolDescription.version = data[2];// 协议版本
			protocolDescription.productID = data[3];// 产品号
			protocolDescription.deviceID = data[4];// 设备号
			protocolDescription.reservedbit = (char) (data[5] << 8 | data[6]);// 保留位
			break;
		case 1:// 心电模块
			data = dataFrameEvent.getData();
			modelType = data[0];
			length = data[1] << 8 | data[2];
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) (data[7] << 8 | data[8]);// 保留位
			} else if (modelType == 1) {// 模块数据
				ecg.breathingrate = data[3] << 8 | data[4];
				ecg.ST1 = data[5] << 8 | data[6];
				ecg.ST2 = data[7] << 8 | data[8];
				ecg.ST3 = data[9] << 8 | data[10];
				ecg.heartrate = data[11] << 8 | data[12];
				int k = 0;
				int i = 0;
				while (k < 500) {
					ecg.ecg1[k++] = (char) (data[i++] << 8 | data[i++]);
				}
				k = 0;
				while (k < 500) {
					ecg.ecg2[k++] = (char) (data[i++] << 8 | data[i++]);
				}
				k = 0;
				while (k < 500) {
					ecg.ecg3[k++] = (char) (data[i++] << 8 | data[i++]);
				}
				ecg.flag = data[i];
			} else if (modelType == 2) {// 模块状态
				ecgState.wirestate = data[3];
				ecgState.reservedbit = data[4];
				ecgState.overloadinfo = data[5];
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) (data[4] << 8 | data[5]);// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
			}
			break;
		case 2:// 血氧模块
			data = dataFrameEvent.getData();
			modelType = data[0];
			length = data[1] << 8 | data[2];
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) (data[7] << 8 | data[8]);// 保留位
			} else if (modelType == 1) {// 模块数据
				bloodOxygen.pulserate = data[3] << 8 | data[4];
				bloodOxygen.saturation = data[5];
				bloodOxygen.pulseintensity = data[6];
				System.arraycopy(data, 7, bloodOxygen.waveform, 0, 125);
				System.arraycopy(data, 132, bloodOxygen.oxygenmark, 0, 125);
			} else if (modelType == 2) {// 模块状态	
				bloodOxygenState.state = data[3];
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) (data[4] << 8 | data[5]);// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
			}
			break;
		case 3:// 血压模块
			data = dataFrameEvent.getData();
			modelType = data[0];
			length = data[1] << 8 | data[2];
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) (data[7] << 8 | data[8]);// 保留位
			} else if (modelType == 1) {// 模块数据
				bloodPressure.systolicpressure = data[3] << 8 | data[4];
				bloodPressure.diastolicpressure = data[5] << 8 | data[6];
				bloodPressure.meanpressure = data[7] << 8 | data[8];
				bloodPressure.pulserate = data[9] << 8 | data[10];
			} else if (modelType == 2) {// 模块状态
				bloodPressureState.cuffpressure = (char) (data[3] << 8 | data[4]);
				bloodPressureState.state = data[5];
				bloodPressureState.type = data[6];
				bloodPressureState.error = data[7];
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) (data[4] << 8 | data[5]);// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
			}
			break;
		case 4:// 血糖模块
			data = dataFrameEvent.getData();
			modelType = data[0];
			length = data[1] << 8 | data[2];
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) (data[7] << 8 | data[8]);// 保留位
			} else if (modelType == 1) {// 模块数据
				bloodSugar.bloodsugar = data[3] << 24 | data[4] << 16
						| data[5] << 8 | data[6];
			} else if (modelType == 2) {// 模块状态
				bloodSugarState.state = data[3];
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) (data[4] << 8 | data[5]);// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
			}

			break;
		case 5:// 血酮模块
			data = dataFrameEvent.getData();
			modelType = data[0];
			length = data[1] << 8 | data[2];
			if (modelType == 0) {// 模块描述
				info.protocolversion = data[3];// 协议版本
				info.reservedbit = data[4];// 保留位
				info.supportcommand = data[5];// 支持的命令
				info.productID = data[6];// 模块产品号
				info.reservedbit2 = (char) (data[7] << 8 | data[8]);// 保留位
			} else if (modelType == 1) {// 模块数据
				bloodKetone.bloodketone = data[3] << 24 | data[4] << 16
						| data[5] << 8 | data[6];
			} else if (modelType == 2) {// 模块状态
				bloodKetoneState.state = data[3];
			} else if (modelType == 3) {// 模块命令
				commandResult.command = data[3];// 命令
				commandResult.length = (char) (data[4] << 8 | data[5]);// 长度
				commandResult.response = data[6];// 响应代码，返回非0为请求错误
				commandResult.reservedbit = data[7];// 保留位
				commandResult.data = Arrays.copyOfRange(data, 8,
						8 + commandResult.length);
			}
			break;
		default:
			break;
		}
	}

}
