package com.kingtic.KingticIO.impl;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.xmlrpc.XmlRpcException;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Div;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.annotation.Label;
import com.ur.urcap.api.ui.annotation.Select;
import com.ur.urcap.api.ui.component.DivComponent;
import com.ur.urcap.api.ui.component.InputButton;
import com.ur.urcap.api.ui.component.InputEvent;
import com.ur.urcap.api.ui.component.InputRadioButton;
import com.ur.urcap.api.ui.component.InputTextField;
import com.ur.urcap.api.ui.component.LabelComponent;
import com.ur.urcap.api.ui.component.SelectDropDownList;
import com.ur.urcap.api.ui.component.SelectList;
import com.kingtic.KingticIO.impl.MyDaemonDaemonService;
import com.kingtic.KingticIO.impl.XmlRpcMyDaemonInterface;

public class KingticIOInstallationNodeContribution implements InstallationNodeContribution {
	class ListBoxItem{
		public InputRadioButton radio;
		public LabelComponent label;
		public ListBoxItem(InputRadioButton radio, LabelComponent label)
		{
			this.radio = radio;
			this.label = label;
		}
	}
	
	//io名，地址，权限（0：启用，1：程序员操作，2：禁用）
	private String[] addrs={"k_di_0,0,0"
			, "k_di_1,1,0"
			, "k_di_2,2,0"
			, "k_di_3,3,0"
			, "k_di_4,4,0"
			, "k_di_5,5,0"
			, "k_di_6,6,0"
			, "k_di_7,7,0"
			, "k_di_8,8,0"
			, "k_di_9,9,0"
			, "k_di_10,10,0"
			, "k_di_11,11,0"
			, "k_di_12,12,0"
			, "k_di_13,13,0"
			, "k_di_14,14,0"
			, "k_di_15,15,0"
			
			,"k_do_0,0,0"
			, "k_do_1,1,0"
			, "k_do_2,2,0"
			, "k_do_3,3,0"
			, "k_do_4,4,0"
			, "k_do_5,5,0"
			, "k_do_6,6,0"
			, "k_do_7,7,0"
			, "k_do_8,8,0"
			, "k_do_9,9,0"
			, "k_do_10,10,0"
			, "k_do_11,11,0"
			, "k_do_12,12,0"
			, "k_do_13,13,0"
			, "k_do_14,14,0"
			, "k_do_15,15,0"
	};
	

	private ResourceBundle KingticStrings;
	private static final String ENABLED_KEY = "enabled";
	private static final String XMLRPC_VARIABLE = "my_daemon";
	private final static String IMAGE_RED = "com/kingtic/KingticIO/impl/red.png";
	private final static String IMAGE_GRAY = "com/kingtic/KingticIO/impl/gray.png";
	
	private final static int INDENTITY_ADDR = 2000;
	private final static int INDENTITY_VALUE = 2000;
	private final static String DEFAULT_IP = "192.168.1.37";
	
	private boolean isConnected = false;
	private boolean isVaild = false;
	private String IP = "";
	private int DO = 0;
	
	//ArrayList<Object> ioItems = new ArrayList<Object>();
	ArrayList<KingticIO> ioInfo = new ArrayList<KingticIO>();
	
	ArrayList<InputButton> ioBtn = new ArrayList<InputButton>();
	ArrayList<LabelComponent> ioBtn_label = new ArrayList<LabelComponent>();
	ArrayList<ListBoxItem> ioListBox = new ArrayList<ListBoxItem>();

	private DataModel model;
	
	private final MyDaemonDaemonService daemonService;
	private XmlRpcMyDaemonInterface xmlRpcDaemonInterface;
	private Timer uiTimer;
	
	@Label(id="logo")
	private LabelComponent lblLogo;
	
	private BufferedImage img_logo;
	private BufferedImage img_red, img_gray;
	private BufferedImage img_tabMonitor, img_tabSetting, img_tabMonitor_sel
	, img_tabSetting_sel, img_hline_tab, img_hline,
	img_success, img_failed, img_disconnected, img_invalid;

	public KingticIOInstallationNodeContribution(MyDaemonDaemonService daemonService, ResourceBundle KingticStrings, DataModel model) {
		this.daemonService = daemonService;
		this.model = model;
		//this.KingticStrings = ResourceBundle.getBundle("KingticIO", Locale.getDefault());
		this.KingticStrings = KingticStrings;
		//
		img_logo = loadImage("com/kingtic/KingticIO/impl/logo1.png");
		img_red = loadImage(IMAGE_RED);
		img_gray = loadImage(IMAGE_GRAY);
		String suffix = KingticStrings.getString("suffix");
		img_tabMonitor = loadImage("com/kingtic/KingticIO/impl/IOmonitor"+suffix+".png");
		img_tabMonitor_sel = loadImage("com/kingtic/KingticIO/impl/IOmonitor-sel"+suffix+".png");
		img_tabSetting = loadImage("com/kingtic/KingticIO/impl/IOsetting"+suffix+".png");
		img_tabSetting_sel = loadImage("com/kingtic/KingticIO/impl/IOsetting-sel"+suffix+".png");
		img_hline_tab = loadImage("com/kingtic/KingticIO/impl/hline_tab.png");
		img_hline = loadImage("com/kingtic/KingticIO/impl/hline.png");
		
		img_success = loadImage("com/kingtic/KingticIO/impl/success"+suffix+".png");
		img_disconnected = loadImage("com/kingtic/KingticIO/impl/disconnected"+suffix+".png");
		img_invalid = loadImage("com/kingtic/KingticIO/impl/invalid"+suffix+".png");
		img_failed = loadImage("com/kingtic/KingticIO/impl/failed"+suffix+".png");
		
		//读IO配置信息  
		for(int i=0; i<addrs.length; ++i)
		{
			String[] addr = addrs[i].split(",");
			ioInfo.add(new KingticIO(addr[0], addr[0], addr[1], addr[2]));
		}
		
		//根据用户设定更新ioInfo
		for(int i=0; i<ioInfo.size(); ++i)
		{
			KingticIO io = ioInfo.get(i);
			if(model.isSet(io.defaultName))
			{
				String[] vals = model.get(io.defaultName, io.defaultName+","+io.rule).split(",");
				
				if(vals.length == 2)
				{
					io.displayName = vals[0];
					io.rule = vals[1];
				}
			}
		}

		xmlRpcDaemonInterface = new XmlRpcMyDaemonInterface("127.0.0.1", 40404);
	}
	
	public void initListBox(){
		ioListBox.clear();
		ioListBox.add(new ListBoxItem(radioIn0,lbIn0));
		ioListBox.add(new ListBoxItem(radioIn1,lbIn1));
		ioListBox.add(new ListBoxItem(radioIn2,lbIn2));
		ioListBox.add(new ListBoxItem(radioIn3,lbIn3));
		ioListBox.add(new ListBoxItem(radioIn4,lbIn4));
		ioListBox.add(new ListBoxItem(radioIn5,lbIn5));
		ioListBox.add(new ListBoxItem(radioIn6,lbIn6));
		ioListBox.add(new ListBoxItem(radioIn7,lbIn7));
		ioListBox.add(new ListBoxItem(radioIn8,lbIn8));
		ioListBox.add(new ListBoxItem(radioIn9,lbIn9));
		ioListBox.add(new ListBoxItem(radioIn10,lbIn10));
		ioListBox.add(new ListBoxItem(radioIn11,lbIn11));
		ioListBox.add(new ListBoxItem(radioIn12,lbIn12));
		ioListBox.add(new ListBoxItem(radioIn13,lbIn13));
		ioListBox.add(new ListBoxItem(radioIn14,lbIn14));
		ioListBox.add(new ListBoxItem(radioIn15,lbIn15));
		
		ioListBox.add(new ListBoxItem(radioOut0,lbOut0));
		ioListBox.add(new ListBoxItem(radioOut1,lbOut1));
		ioListBox.add(new ListBoxItem(radioOut2,lbOut2));
		ioListBox.add(new ListBoxItem(radioOut3,lbOut3));
		ioListBox.add(new ListBoxItem(radioOut4,lbOut4));
		ioListBox.add(new ListBoxItem(radioOut5,lbOut5));
		ioListBox.add(new ListBoxItem(radioOut6,lbOut6));
		ioListBox.add(new ListBoxItem(radioOut7,lbOut7));
		ioListBox.add(new ListBoxItem(radioOut8,lbOut8));
		ioListBox.add(new ListBoxItem(radioOut9,lbOut9));
		ioListBox.add(new ListBoxItem(radioOut10,lbOut10));
		ioListBox.add(new ListBoxItem(radioOut11,lbOut11));
		ioListBox.add(new ListBoxItem(radioOut12,lbOut12));
		ioListBox.add(new ListBoxItem(radioOut13,lbOut13));
		ioListBox.add(new ListBoxItem(radioOut14,lbOut14));
		ioListBox.add(new ListBoxItem(radioOut15,lbOut15));
		
		//io name
		for(int i=0; i<ioListBox.size(); ++i)
		{
			//System.out.println(model.get(ioInfo.get(i).defaultName, "1"));
			String ioName = ioInfo.get(i).displayName;
			ioListBox.get(i).label.setText(ioName);
		}
	}
	
	public void initIObtn(){
		ioBtn.clear();
		ioBtn_label.clear();
		ioBtn_label.add(ki0);
		ioBtn_label.add(ki1);
		ioBtn_label.add(ki2);
		ioBtn_label.add(ki3);
		ioBtn_label.add(ki4);
		ioBtn_label.add(ki5);
		ioBtn_label.add(ki6);
		ioBtn_label.add(ki7);
		ioBtn_label.add(ki8);
		ioBtn_label.add(ki9);
		ioBtn_label.add(ki10);
		ioBtn_label.add(ki11);
		ioBtn_label.add(ki12);
		ioBtn_label.add(ki13);
		ioBtn_label.add(ki14);
		ioBtn_label.add(ki15);
		
		ioBtn.add(ko0);
		ioBtn.add(ko1);
		ioBtn.add(ko2);
		ioBtn.add(ko3);
		ioBtn.add(ko4);
		ioBtn.add(ko5);
		ioBtn.add(ko6);
		ioBtn.add(ko7);
		ioBtn.add(ko8);
		ioBtn.add(ko9);
		ioBtn.add(ko10);
		ioBtn.add(ko11);
		ioBtn.add(ko12);
		ioBtn.add(ko13);
		ioBtn.add(ko14);
		ioBtn.add(ko15);
		
		for(int i=0; i<ioBtn_label.size(); ++i)
		{
			ioBtn_label.get(i).setImage(img_gray);
		}
		
		for(int i=0; i<ioBtn.size(); ++i)
		{
			ioBtn.get(i).setImage(img_gray);
		}
	}
	
	
	@Label(id = "IPTextLabel")
	private LabelComponent IPTextLabel;
	@Label(id = "renameLabel")
	private LabelComponent renameLabel;
	@Label(id = "inputLabel")
	private LabelComponent inputLabel;
	@Label(id = "outputLabel")
	private LabelComponent outputLabel;
	@Label(id = "inputLabel_set")
	private LabelComponent inputLabel_set;
	@Label(id = "outputLabel_set")
	private LabelComponent outputLabel_set;
	@Input(id = "txtIP")
	private InputTextField IPText;
	
	@Input(id = "btnConnectTCP")
	private InputButton connectTCPButton;
	
	@Label(id = "lblTcpStatus")
	private LabelComponent tcpStatusLabel;
	
	
	@Input(id = "btnConnectTCP")
	public void onConnectClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(daemonService.getDaemon().getState() == DaemonContribution.State.RUNNING)
			{
				try {
					if(!isConnected && !isVaild)
					{
						IP = IPText.getText();
						if(!IP.isEmpty())
						{
							isConnected = xmlRpcDaemonInterface.ConnectTCP(IP);
							if(!isConnected)
							{
								tcpStatusLabel.setImage(img_failed);
							}
						}
					}
					if(isConnected && !isVaild)
					{
						int ret = CheckIndentity();
						if(ret == 0)
						{
							tcpStatusLabel.setImage(img_success);
							connectTCPButton.setText(KingticStrings.getString("Disconnect"));
							isVaild = true;
						}else if(ret == 1)
						{
							tcpStatusLabel.setImage(img_invalid);
						}else
						{
							tcpStatusLabel.setImage(img_failed);
						}
					}else if(isConnected && isVaild)
					{
						xmlRpcDaemonInterface.Disconnect();
						isConnected = false;
						isVaild = false;
						//tcpStatusLabel.setText("连接已断开");
						tcpStatusLabel.setImage(img_disconnected);
						connectTCPButton.setText(KingticStrings.getString("Connect"));
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//tcpStatusLabel.setText("连接失败");
					tcpStatusLabel.setImage(img_failed);
					connectTCPButton.setText(KingticStrings.getString("Connect"));
				} 
			}else
			{
				tcpStatusLabel.setText("daemon或Rpc启动失败！");
			}
			
		}
	}
	
	//
	private int CheckIndentity()
	{
		//0:success 1:fail 2:not ready
		int ret = 1;
		try {
			String indentity = xmlRpcDaemonInterface.GetIO(INDENTITY_ADDR+",1");
			Integer value = new Integer(indentity);
			if(value==INDENTITY_VALUE)
			{
				ret = 0;
			}
		} catch (XmlRpcException e) {
			ret = 2;
			e.printStackTrace();
		} catch (UnknownResponseException e) {
			ret = 2;
			e.printStackTrace();
		}
		return ret;
	}
	
	@Input(id = "txtioName")
	private InputTextField ioNameText;
	@Input(id = "btnClean")
	private InputButton cleanBotton;
	
	@Input(id = "radioIn0")
	private InputRadioButton radioIn0;
	@Input(id = "radioIn1")
	private InputRadioButton radioIn1;
	@Input(id = "radioIn2")
	private InputRadioButton radioIn2;
	@Input(id = "radioIn3")
	private InputRadioButton radioIn3;
	@Input(id = "radioIn4")
	private InputRadioButton radioIn4;
	@Input(id = "radioIn5")
	private InputRadioButton radioIn5;
	@Input(id = "radioIn6")
	private InputRadioButton radioIn6;
	@Input(id = "radioIn7")
	private InputRadioButton radioIn7;
	@Input(id = "radioIn8")
	private InputRadioButton radioIn8;
	@Input(id = "radioIn9")
	private InputRadioButton radioIn9;
	@Input(id = "radioIn10")
	private InputRadioButton radioIn10;
	@Input(id = "radioIn11")
	private InputRadioButton radioIn11;
	@Input(id = "radioIn12")
	private InputRadioButton radioIn12;
	@Input(id = "radioIn13")
	private InputRadioButton radioIn13;
	@Input(id = "radioIn14")
	private InputRadioButton radioIn14;
	@Input(id = "radioIn15")
	private InputRadioButton radioIn15;
	
	@Label(id = "lbIn0")
	private LabelComponent lbIn0;
	@Label(id = "lbIn1")
	private LabelComponent lbIn1;
	@Label(id = "lbIn2")
	private LabelComponent lbIn2;
	@Label(id = "lbIn3")
	private LabelComponent lbIn3;
	@Label(id = "lbIn4")
	private LabelComponent lbIn4;
	@Label(id = "lbIn5")
	private LabelComponent lbIn5;
	@Label(id = "lbIn6")
	private LabelComponent lbIn6;
	@Label(id = "lbIn7")
	private LabelComponent lbIn7;
	@Label(id = "lbIn8")
	private LabelComponent lbIn8;
	@Label(id = "lbIn9")
	private LabelComponent lbIn9;
	@Label(id = "lbIn10")
	private LabelComponent lbIn10;
	@Label(id = "lbIn11")
	private LabelComponent lbIn11;
	@Label(id = "lbIn12")
	private LabelComponent lbIn12;
	@Label(id = "lbIn13")
	private LabelComponent lbIn13;
	@Label(id = "lbIn14")
	private LabelComponent lbIn14;
	@Label(id = "lbIn15")
	private LabelComponent lbIn15;
	
	@Input(id = "radioOut0")
	private InputRadioButton radioOut0;
	@Input(id = "radioOut1")
	private InputRadioButton radioOut1;
	@Input(id = "radioOut2")
	private InputRadioButton radioOut2;
	@Input(id = "radioOut3")
	private InputRadioButton radioOut3;
	@Input(id = "radioOut4")
	private InputRadioButton radioOut4;
	@Input(id = "radioOut5")
	private InputRadioButton radioOut5;
	@Input(id = "radioOut6")
	private InputRadioButton radioOut6;
	@Input(id = "radioOut7")
	private InputRadioButton radioOut7;
	@Input(id = "radioOut8")
	private InputRadioButton radioOut8;
	@Input(id = "radioOut9")
	private InputRadioButton radioOut9;
	@Input(id = "radioOut10")
	private InputRadioButton radioOut10;
	@Input(id = "radioOut11")
	private InputRadioButton radioOut11;
	@Input(id = "radioOut12")
	private InputRadioButton radioOut12;
	@Input(id = "radioOut13")
	private InputRadioButton radioOut13;
	@Input(id = "radioOut14")
	private InputRadioButton radioOut14;
	@Input(id = "radioOut15")
	private InputRadioButton radioOut15;
	
	@Label(id = "lbOut0")
	private LabelComponent lbOut0;
	@Label(id = "lbOut1")
	private LabelComponent lbOut1;
	@Label(id = "lbOut2")
	private LabelComponent lbOut2;
	@Label(id = "lbOut3")
	private LabelComponent lbOut3;
	@Label(id = "lbOut4")
	private LabelComponent lbOut4;
	@Label(id = "lbOut5")
	private LabelComponent lbOut5;
	@Label(id = "lbOut6")
	private LabelComponent lbOut6;
	@Label(id = "lbOut7")
	private LabelComponent lbOut7;
	@Label(id = "lbOut8")
	private LabelComponent lbOut8;
	@Label(id = "lbOut9")
	private LabelComponent lbOut9;
	@Label(id = "lbOut10")
	private LabelComponent lbOut10;
	@Label(id = "lbOut11")
	private LabelComponent lbOut11;
	@Label(id = "lbOut12")
	private LabelComponent lbOut12;
	@Label(id = "lbOut13")
	private LabelComponent lbOut13;
	@Label(id = "lbOut14")
	private LabelComponent lbOut14;
	@Label(id = "lbOut15")
	private LabelComponent lbOut15;
	
	@Input(id = "radioIn0")
	private void selectRadioIn0(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn0.isSelected())
			{
				setTextValue(0);
			}
		}
	}
	@Input(id = "radioIn1")
	private void selectRadioIn1(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn1.isSelected())
			{
				setTextValue(1);
			}
		}
	}
	@Input(id = "radioIn2")
	private void selectRadioIn2(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn2.isSelected())
			{
				setTextValue(2);
			}
		}
	}
	@Input(id = "radioIn3")
	private void selectRadioIn3(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn3.isSelected())
			{
				setTextValue(3);
			}
		}
	}
	@Input(id = "radioIn4")
	private void selectRadioIn4(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn4.isSelected())
			{
				setTextValue(4);
			}
		}
	}
	@Input(id = "radioIn5")
	private void selectRadioIn5(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn5.isSelected())
			{
				setTextValue(5);
			}
		}
	}
	@Input(id = "radioIn6")
	private void selectRadioIn6(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn6.isSelected())
			{
				setTextValue(6);
			}
		}
	}
	@Input(id = "radioIn7")
	private void selectRadioIn7(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn7.isSelected())
			{
				setTextValue(7);
			}
		}
	}
	@Input(id = "radioIn8")
	private void selectRadioIn8(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn8.isSelected())
			{
				setTextValue(8);
			}
		}
	}
	@Input(id = "radioIn9")
	private void selectRadioIn9(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn9.isSelected())
			{
				setTextValue(9);
			}
		}
	}
	@Input(id = "radioIn10")
	private void selectRadioIn10(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn10.isSelected())
			{
				setTextValue(10);
			}
		}
	}
	@Input(id = "radioIn11")
	private void selectRadioIn11(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn11.isSelected())
			{
				setTextValue(11);
			}
		}
	}
	@Input(id = "radioIn12")
	private void selectRadioIn12(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn12.isSelected())
			{
				setTextValue(12);
			}
		}
	}
	@Input(id = "radioIn13")
	private void selectRadioIn13(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn13.isSelected())
			{
				setTextValue(13);
			}
		}
	}
	@Input(id = "radioIn14")
	private void selectRadioIn14(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn14.isSelected())
			{
				setTextValue(14);
			}
		}
	}
	@Input(id = "radioIn15")
	private void selectRadioIn15(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioIn15.isSelected())
			{
				setTextValue(15);
			}
		}
	}
	@Input(id = "radioOut0")
	private void selectRadioOut0(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut0.isSelected())
			{
				setTextValue(16);
			}
		}
	}
	@Input(id = "radioOut1")
	private void selectRadioOut1(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut1.isSelected())
			{
				setTextValue(17);
			}
		}
	}
	@Input(id = "radioOut2")
	private void selectRadioOut2(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut2.isSelected())
			{
				setTextValue(18);
			}
		}
	}
	@Input(id = "radioOut3")
	private void selectRadioOut3(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut3.isSelected())
			{
				setTextValue(19);
			}
		}
	}
	@Input(id = "radioOut4")
	private void selectRadioOut4(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut4.isSelected())
			{
				setTextValue(20);
			}
		}
	}
	@Input(id = "radioOut5")
	private void selectRadioOut5(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut5.isSelected())
			{
				setTextValue(21);
			}
		}
	}
	@Input(id = "radioOut6")
	private void selectRadioOut6(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut6.isSelected())
			{
				setTextValue(22);
			}
		}
	}
	@Input(id = "radioOut7")
	private void selectRadioOut7(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut7.isSelected())
			{
				setTextValue(23);
			}
		}
	}
	@Input(id = "radioOut8")
	private void selectRadioOut8(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut8.isSelected())
			{
				setTextValue(24);
			}
		}
	}
	@Input(id = "radioOut9")
	private void selectRadioOut9(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut9.isSelected())
			{
				setTextValue(25);
			}
		}
	}
	@Input(id = "radioOut10")
	private void selectRadioOut10(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut10.isSelected())
			{
				setTextValue(26);
			}
		}
	}
	@Input(id = "radioOut11")
	private void selectRadioOut11(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut11.isSelected())
			{
				setTextValue(27);
			}
		}
	}
	@Input(id = "radioOut12")
	private void selectRadioOut12(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut12.isSelected())
			{
				setTextValue(28);
			}
		}
	}
	@Input(id = "radioOut13")
	private void selectRadioOut13(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut13.isSelected())
			{
				setTextValue(29);
			}
		}
	}
	@Input(id = "radioOut14")
	private void selectRadioOut14(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut14.isSelected())
			{
				setTextValue(30);
			}
		}
	}
	@Input(id = "radioOut15")
	private void selectRadioOut15(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(radioOut15.isSelected())
			{
				setTextValue(31);
			}
		}
	}
	
	private void setTextValue(int idx)
	{
		KingticIO io = ioInfo.get(idx);
		ioNameText.setText(io.displayName);
	}
	
	
	@Label(id = "ki0")
	private LabelComponent ki0;
	@Label(id = "ki1")
	private LabelComponent ki1;
	@Label(id = "ki2")
	private LabelComponent ki2;
	@Label(id = "ki3")
	private LabelComponent ki3;
	@Label(id = "ki4")
	private LabelComponent ki4;
	@Label(id = "ki5")
	private LabelComponent ki5;
	@Label(id = "ki6")
	private LabelComponent ki6;
	@Label(id = "ki7")
	private LabelComponent ki7;
	@Label(id = "ki8")
	private LabelComponent ki8;
	@Label(id = "ki9")
	private LabelComponent ki9;
	@Label(id = "ki10")
	private LabelComponent ki10;
	@Label(id = "ki11")
	private LabelComponent ki11;
	@Label(id = "ki12")
	private LabelComponent ki12;
	@Label(id = "ki13")
	private LabelComponent ki13;
	@Label(id = "ki14")
	private LabelComponent ki14;
	@Label(id = "ki15")
	private LabelComponent ki15;
	
	@Input(id = "ko0")
	private InputButton ko0;
	@Input(id = "ko1")
	private InputButton ko1;
	@Input(id = "ko2")
	private InputButton ko2;
	@Input(id = "ko3")
	private InputButton ko3;
	@Input(id = "ko4")
	private InputButton ko4;
	@Input(id = "ko5")
	private InputButton ko5;
	@Input(id = "ko6")
	private InputButton ko6;
	@Input(id = "ko7")
	private InputButton ko7;
	@Input(id = "ko8")
	private InputButton ko8;
	@Input(id = "ko9")
	private InputButton ko9;
	@Input(id = "ko10")
	private InputButton ko10;
	@Input(id = "ko11")
	private InputButton ko11;
	@Input(id = "ko12")
	private InputButton ko12;
	@Input(id = "ko13")
	private InputButton ko13;
	@Input(id = "ko14")
	private InputButton ko14;
	@Input(id = "ko15")
	private InputButton ko15;
	
	@Input(id = "ko0")
	public void onko0Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(0);
		}
	}
	@Input(id = "ko1")
	public void onko1Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(1);
		}
	}
	@Input(id = "ko2")
	public void onko2Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(2);
		}
	}
	@Input(id = "ko3")
	public void onko3Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(3);
		}
	}
	@Input(id = "ko4")
	public void onko4Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(4);
		}
	}
	@Input(id = "ko5")
	public void onko5Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(5);
		}
	}
	@Input(id = "ko6")
	public void onko6Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(6);
		}
	}
	@Input(id = "ko7")
	public void onko7Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(7);
		}
	}
	@Input(id = "ko8")
	public void onko8Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(8);
		}
	}
	@Input(id = "ko9")
	public void onko9Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(9);
		}
	}
	@Input(id = "ko10")
	public void onko10Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(10);
		}
	}
	@Input(id = "ko11")
	public void onko11Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(11);
		}
	}
	@Input(id = "ko12")
	public void onko12Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(12);
		}
	}
	@Input(id = "ko13")
	public void onko13Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(13);
		}
	}
	@Input(id = "ko14")
	public void onko14Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(14);
		}
	}
	@Input(id = "ko15")
	public void onko15Click(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			SetDOByIdx(15);
		}
	}
	
	@Input(id = "txtioName")
	public void onMessageChange(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			
			int idx = -1;
			ListBoxItem item = null;
			for(int i=0; i<ioListBox.size(); ++i)
			{
				idx = i;
				item = ioListBox.get(i);
				if(item.radio.isSelected())
				{
					break;
				}
			}
			if(idx != -1)
			{
				String val = ioNameText.getText();
				
				KingticIO io = ioInfo.get(idx);
				if(!val.isEmpty())
				{
					io.displayName = val;
					model.set(io.defaultName, val+","+io.rule);
				}else
				{
					io.displayName = io.defaultName;
					model.remove(io.defaultName);
				}
				
				item.label.setText(io.displayName);
			}
			
		}
	}
	
	@Input(id = "btnClean")
	public void onCleanClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			int idx = -1;
			ListBoxItem item = null;
			for(int i=0; i<ioListBox.size(); ++i)
			{
				idx = i;
				item = ioListBox.get(i);
				if(item.radio.isSelected())
				{
					break;
				}
			}
			if(idx != -1)
			{
				KingticIO io = ioInfo.get(idx);
				io.displayName = io.defaultName;
				model.remove(io.defaultName);
				
				item.label.setText(io.displayName);
			}
		}
	}
	
	///tab
	@Input(id = "tabIoSetting")
	private InputButton tabIoSetting;
	@Input(id = "tabIoMonitor")
	private InputButton tabIoMonitor;
	@Label(id="hline1")
	private LabelComponent hline1;
	@Label(id="hline2")
	private LabelComponent hline2;
	@Label(id="hline3")
	private LabelComponent hline3;
	@Label(id="hline4")
	private LabelComponent hline4;
	
	@Div(id="divIoSetting")
	private DivComponent divIOSetting;
	@Div(id="divIoMonitor")
	private DivComponent divIOMonitor;
	
	@Input(id = "tabIoSetting")
	public void ontabSettingClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_RELEASED) {
			divIOSetting.setVisible(true);
			divIOMonitor.setVisible(false);
//			tabIoSetting.setEnabled(false);
//			tabIoMonitor.setEnabled(true);
			tabIoSetting.setImage(img_tabSetting_sel);
			tabIoMonitor.setImage(img_tabMonitor);
		}
	}
	
	@Input(id = "tabIoMonitor")
	public void ontabMonitorClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_RELEASED) {
			divIOMonitor.setVisible(true);
			divIOSetting.setVisible(false);
//			tabIoSetting.setEnabled(true);
//			tabIoMonitor.setEnabled(false);
			tabIoSetting.setImage(img_tabSetting);
			tabIoMonitor.setImage(img_tabMonitor_sel);
		}
	}

	@Override
	public void openView() {
		lblLogo.setImage(img_logo);
		IPTextLabel.setText(KingticStrings.getString("INodeC_IPLabel"));
		renameLabel.setText(KingticStrings.getString("Rename"));
		inputLabel.setText(KingticStrings.getString("INode_InputLabel"));
		outputLabel.setText(KingticStrings.getString("INode_OutputLabel"));
		inputLabel_set.setText(KingticStrings.getString("INode_InputLabel"));
		outputLabel_set.setText(KingticStrings.getString("INode_OutputLabel"));
		divIOMonitor.setVisible(true);
		divIOSetting.setVisible(false);
		tabIoSetting.setImage(img_tabSetting);
		tabIoMonitor.setImage(img_tabMonitor_sel);
		hline1.setImage(img_hline_tab);
		hline2.setImage(img_hline);
		hline3.setImage(img_hline);
		hline4.setImage(img_hline);
		
		IPText.setText(DEFAULT_IP);
		
		connectTCPButton.setText(KingticStrings.getString("Connect"));
		cleanBotton.setText(KingticStrings.getString("Clean"));
		//io btn
		initIObtn();
		initListBox();
		
		//applyDesiredDaemonStatus();
		//UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 100);
	}
	
	private void SetDOByIdx(int idx)
	{
		if(isVaild)
		{
			int ret = DO ^ (1 << idx);
			 try {
				xmlRpcDaemonInterface.SendCommand("1,"+ret);
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private BufferedImage loadImage(String path) {
		BufferedImage bufferedImage = null;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(path));
		try {
			bufferedImage = ImageIO.read(bufferedInputStream);
		} catch (IOException e) {
			System.err.println("Unable to load image: " + path);
		} finally {
			if (bufferedImage != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					System.err.println("Failed to close image input stream " + e.getMessage());
				}
			}
		}
		return bufferedImage;
	}
	
	private void updateUI() {
		try {
			if(isVaild)
			{
				String ret_in = xmlRpcDaemonInterface.GetIO("0,1");
				String ret_out = xmlRpcDaemonInterface.GetIO("1,1");
				if(!ret_in.isEmpty() && !ret_out.isEmpty())
				{
					Boolean[] status_in = new Boolean[16];
					Boolean[] status_out = new Boolean[16];
					Integer val_in = new Integer(ret_in);
					Integer val_out = new Integer(ret_out);
					DO = val_out;
					for(int i=0; i<16; i++)
					{
						status_in[i] = ((val_in>>i) & 0x1) == 0 ? false : true;
						status_out[i] = ((val_out>>i) & 0x1) == 0 ? false : true;
					}
					for(int i=0; i<16; i++)
					{
						if(status_in[i])
						{
							ioBtn_label.get(i).setImage(img_red);
						}else
						{
							ioBtn_label.get(i).setImage(img_gray);
						}
						
						if(status_out[i])
						{
							ioBtn.get(i).setImage(img_red);
						}else
						{
							ioBtn.get(i).setImage(img_gray);
						}
					}
				}
			}else
			{
				for(int i=0; i<16; i++)
				{
					ioBtn_label.get(i).setImage(img_gray);
					ioBtn.get(i).setImage(img_gray);
				}
			}
			
		} catch (XmlRpcException e) {//connect broken
			isConnected = false;
			isVaild = false;
			//daemonService.getDaemon().stop();
			//daemonService.getDaemon().start();
			try {
				boolean ret = xmlRpcDaemonInterface.Disconnect();
				//System.out.println("duankai:"+ret);
			} catch (XmlRpcException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownResponseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//tcpStatusLabel.setText("连接已断开");
			tcpStatusLabel.setImage(img_disconnected);
			connectTCPButton.setText(KingticStrings.getString("Connect"));
			e.printStackTrace();
		} catch (UnknownResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Boolean[] getStatusFromHoldingRegisters() throws XmlRpcException, UnknownResponseException
	{
		Boolean[] status = new Boolean[16];
		String ret = xmlRpcDaemonInterface.GetIO("0,1");
		Short val = new Short(ret);
		for(int i=0; i<16; i++)
		{
			status[i] = ((val>>i) & 0x1) == 0 ? false : true;
		}
		return status;
	}

	@Override
	public void closeView() { 
		if (uiTimer != null) {
			uiTimer.cancel();
		}
	}


	@Override
	public void generateScript(ScriptWriter writer) {
		writer.globalVariable(XMLRPC_VARIABLE, "rpc_factory(\"xmlrpc\", \"http://127.0.0.1:40404/RPC2\")");
		// Apply the settings to the daemon on program start in the Installation pre-amble
		writer.appendLine(XMLRPC_VARIABLE + ".connect_TCP(\"" + IP + "\")");
	}
	

	private DaemonContribution.State getDaemonState(){
		return daemonService.getDaemon().getState();
	}

	
	public ArrayList<Object> getIOInputItems()
	{
		ArrayList<Object> ret = new ArrayList<Object>();
		for(int i=0; i<16; ++i)
		{
			ret.add(ioInfo.get(i).displayName);
		}
		return ret;
	}
	
	public ArrayList<Object> getIOOutputItems()
	{
		ArrayList<Object> ret = new ArrayList<Object>();
		for(int i=16; i<32; ++i)
		{
			ret.add(ioInfo.get(i).displayName);
		}
		return ret;
	}
	
	public KingticIO getIO(int idx)
	{
		return ioInfo.get(idx);
	}
	
	public KingticIO getIOOutput(int idx)
	{
		return ioInfo.get(16+idx);
	}
	
	public KingticIO getIOInput(int idx)
	{
		return ioInfo.get(idx);
	}
	
	public String getXMLRPCVariable() {return XMLRPC_VARIABLE;}
	
	public XmlRpcMyDaemonInterface getXmlRpcDaemonInterface() {return xmlRpcDaemonInterface; }
	
	public boolean isConnected()
	{
		return isVaild;
	}
	
	public ResourceBundle getLocalResource(){
		return this.KingticStrings;
	}

}
