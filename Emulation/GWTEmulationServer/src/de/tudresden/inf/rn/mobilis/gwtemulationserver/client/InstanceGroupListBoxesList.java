package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

public class InstanceGroupListBoxesList {
	
	private List<ListBox> listBoxes;
	private int firstInstanceId;
	
	public InstanceGroupListBoxesList(List<ListBox> listBoxes, int firstInstanceId) {
		this.listBoxes = listBoxes;
		this.firstInstanceId = firstInstanceId;
	}
	
	public List<ListBox> getListBoxes() {
		return listBoxes;
	}
	
	public void setListBoxes(List<ListBox> listBoxes) {
		this.listBoxes = listBoxes;
	}

	public int getFirstInstanceId() {
		return firstInstanceId;
	}

	public void setFirstInstanceId(int firstInstanceId) {
		this.firstInstanceId = firstInstanceId;
	}

}
