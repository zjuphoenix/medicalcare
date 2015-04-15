package com.zju.medicalcare;

import java.io.IOException;

public class App {

	public static void main(String[] args) {
		Client client = new Client();
        try {
			client.startECG();
			client.dataCollect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
