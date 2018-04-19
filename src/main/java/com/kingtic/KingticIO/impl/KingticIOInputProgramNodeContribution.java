package com.kingtic.KingticIO.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.xmlrpc.XmlRpcException;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Img;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.annotation.Label;
import com.ur.urcap.api.ui.annotation.Select;
import com.ur.urcap.api.ui.component.ImgComponent;
import com.ur.urcap.api.ui.component.InputButton;
import com.ur.urcap.api.ui.component.InputEvent;
import com.ur.urcap.api.ui.component.InputRadioButton;
import com.ur.urcap.api.ui.component.InputTextField;
import com.ur.urcap.api.ui.component.LabelComponent;
import com.ur.urcap.api.ui.component.SelectDropDownList;
import com.ur.urcap.api.ui.component.SelectEvent;

import static com.ur.urcap.api.ui.component.InputEvent.EventType.ON_PRESSED;
import static com.ur.urcap.api.ui.component.SelectEvent.EventType.ON_SELECT;

public class KingticIOInputProgramNodeContribution implements ProgramNodeContribution {
	private static final String SELECTED_IO = "selected_io";
	private static final String RADIO_ON = "radio_on";
	private final static String IMAGE_PATH = "com/kingtic/KingticIO/impl/logo.png";

	private final DataModel model;
	private final URCapAPI api;
	
	private ResourceBundle KingticStrings;
	
	private String DefaultTitle = "";
	private String Wait = "";
	private String On = "";
	private String Off = "";
	private String SetTitle = "";

	public KingticIOInputProgramNodeContribution(URCapAPI api, DataModel model, ResourceBundle KingticStrings) {
		this.api = api;
		this.model = model;
		this.KingticStrings = KingticStrings;
		this.DefaultTitle = KingticStrings.getString("PNodeC_In_Title");
		this.model.set("DefaultTitle", DefaultTitle);
		Wait = KingticStrings.getString("Wait");
		On = KingticStrings.getString("ON");
		Off = KingticStrings.getString("OFF");
	}

	@Label(id="logo")
	private LabelComponent lblLogo;
	@Label(id="labelFront")
	private LabelComponent labelFront;
	
	@Select(id="selInput")
	private SelectDropDownList InputSelect;
	
	@Select(id = "selInput")
	private void selectIO(SelectEvent event) {
		if (event.getEvent() == ON_SELECT) {
			int idx = InputSelect.getSelectedIndex();
			model.set(SELECTED_IO, idx);
			UpdateTitle();
		}
	}
	
	@Input(id="radioOn")
	private InputRadioButton radioOn;
	@Input(id = "radioOn")
	private void selectRadioON(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			model.set(RADIO_ON, radioOn.isSelected());
			UpdateTitle();
		}
	}
	
	@Input(id="radioOff")
	private InputRadioButton radioOff;
	

	@Override
	public void openView() {
		BufferedImage img = loadImage();
		if (img != null) {
			lblLogo.setImage(img);
		}

		labelFront.setText(KingticStrings.getString("Wait"));
		ArrayList<Object> items = getInstallation().getIOInputItems();
		items.add(0, "    ---");
		InputSelect.setItems(items);
		InputSelect.selectItemAtIndex(model.get(SELECTED_IO, 0));
		
		radioOn.setText(On);
		radioOff.setText(Off);
		
		if(model.get(RADIO_ON, true))
			radioOn.setSelected();
		else
			radioOff.setSelected();
	}
	
	private BufferedImage loadImage() {
		BufferedImage bufferedImage = null;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(IMAGE_PATH));
		try {
			bufferedImage = ImageIO.read(bufferedInputStream);
		} catch (IOException e) {
			System.err.println("Unable to load image: " + IMAGE_PATH);
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

	@Override
	public void closeView() {
	}

	@Override
	public String getTitle() {
		return this.model.get("DefaultTitle", DefaultTitle);//SetTitle.isEmpty() ? DefaultTitle : SetTitle;
	}

	@Override
	public boolean isDefined() {
		return getInstallation().isConnected() && InputSelect.getSelectedIndex()!=0;
	}
	
	@Override
	public void generateScript(ScriptWriter writer) {
		if(getInstallation().isConnected())
		{
			int idx = model.get(SELECTED_IO, 0);
			int value = model.get(RADIO_ON, true) ? 1 : 0;
			KingticIO io = getInstallation().getIOInput(idx-1);
			String cmd = io.addr + ",1";
			
			writer.appendLine("while ("+getInstallation().getXMLRPCVariable()+".ReadDiscreteInputs(\""+cmd+"\") != \""+value+"\"):");
			writer.appendLine("  sync()");
			writer.appendLine("end");
		}
		
		writer.writeChildren();
	}

	private void UpdateTitle()
	{
		if(InputSelect.getSelectedIndex()>0)
		{
			SetTitle = Wait+" "
					+InputSelect.getSelectedItem()
					+(radioOn.isSelected()?("="+On):"="+Off);
			this.model.set("DefaultTitle", SetTitle);
		}
	}

	private KingticIOInstallationNodeContribution getInstallation() {
		return api.getInstallationNode(KingticIOInstallationNodeContribution.class);
	}

}
