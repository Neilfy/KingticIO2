package com.kingtic.KingticIO.impl;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class XmlRpcMyDaemonInterface {

	private final XmlRpcClient client;

	public XmlRpcMyDaemonInterface(String host, int port) {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setEnabledForExtensions(true);
		try {
			config.setServerURL(new URL("http://" + host + ":" + port + "/RPC2"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//config.setConnectionTimeout(1000); //1s
		//config.setReplyTimeout(1000);
		client = new XmlRpcClient();
		client.setConfig(config);
	}

	
	public Boolean ConnectTCP(String ip) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(ip);
		Object result = client.execute("connect_TCP", args);
		return processBoolean(result);
	}
	
	public boolean Disconnect() throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		Object result = client.execute("disconnect", args);
		return processBoolean(result);
	}
	
	public boolean IsConnected() throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		Object result = client.execute("isConnected", args);
		return processBoolean(result);
	}
	
	public Boolean SendCommand(String value) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(value);
		Object result = client.execute("send_Command", args);
		return processBoolean(result);
	}
	
	public Boolean WriteSingleCoil(String value) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(value);
		Object result = client.execute("WriteSingleCoil", args);
		return processBoolean(result);
	}
	
	public String GetIO(String value) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(value);
		Object result = client.execute("get_IO", args);
		return processString(result);
	}
	
	public String ReadDiscreteInputs(String value) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(value);
		Object result = client.execute("ReadDiscreteInputs", args);
		return processString(result);
	}
	
	public String ReadCoils(String value) throws XmlRpcException, UnknownResponseException {
		ArrayList<String> args = new ArrayList<String>();
		args.add(value);
		Object result = client.execute("ReadCoils", args);
		return processString(result);
	}


	private boolean processBoolean(Object response) throws UnknownResponseException {
		if (response instanceof Boolean) {
			Boolean val = (Boolean) response;
			return val.booleanValue();
		} else {
			throw new UnknownResponseException();
		}
	}

	private String processString(Object response) throws UnknownResponseException {
		if (response instanceof String) {
			return (String) response;
		} else {
			throw new UnknownResponseException();
		}
	}
}
