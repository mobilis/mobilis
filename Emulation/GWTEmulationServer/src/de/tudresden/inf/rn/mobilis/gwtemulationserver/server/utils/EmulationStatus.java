package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="EmulationStatus")
public class EmulationStatus implements Serializable {
	
	private Long id;
	private ArrayList<EmulationCommand> finishedCommands;
	private ArrayList<EmulationCommand> notFinishedCommands;
	
	public EmulationStatus() {
		this.finishedCommands = new ArrayList<EmulationCommand>();
		this.notFinishedCommands = new ArrayList<EmulationCommand>();
	}
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) {
		this.id = id;
	}
	
	public ArrayList<EmulationCommand> getFinishedCommands() {
		return finishedCommands;
	}
	
	public void setFinishedCommands(ArrayList<EmulationCommand> finishedCommands) {
		this.finishedCommands = finishedCommands;
	}
	
	public ArrayList<EmulationCommand> getNotFinishedCommands() {
		return notFinishedCommands;
	}
	
	public void setNotFinishedCommands(ArrayList<EmulationCommand> notFinishedCommands) {
		this.notFinishedCommands = notFinishedCommands;
	}
	
	@Transient
	public void addFinishedCommand(EmulationCommand cmd) {
		finishedCommands.add(cmd);
	}
	
	@Transient
	public void addNotFinishedCommand(EmulationCommand cmd) {
		notFinishedCommands.add(cmd);
	}

}
