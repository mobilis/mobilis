package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="EmulationStatus")
public class EmulationStatus implements Serializable {
	
	private Long id;
	private List<EmulationCommand> finishedCommands;
	private List<EmulationCommand> notFinishedCommands;
	
	public EmulationStatus() {
		this.finishedCommands = new ArrayList<EmulationCommand>();
		this.notFinishedCommands = new ArrayList<EmulationCommand>();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//@GenericGenerator(name="increment", strategy="increment")
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) {
		this.id = id;
	}
	
	//@OneToMany(targetEntity=EmulationCommand.class, mappedBy="EmulationStatus", fetch=FetchType.EAGER)
	@OneToMany(cascade=CascadeType.ALL)
	//@Fetch(FetchMode.JOIN)
	public List<EmulationCommand> getFinishedCommands() {
		return finishedCommands;
	}
	
	public void setFinishedCommands(List<EmulationCommand> finishedCommands) {
		this.finishedCommands = finishedCommands;
	}
	
	//@OneToMany(targetEntity=EmulationCommand.class, mappedBy="EmulationStatus", fetch=FetchType.EAGER)
	@OneToMany(cascade=CascadeType.ALL)
	//@Fetch(FetchMode.JOIN)
	public List<EmulationCommand> getNotFinishedCommands() {
		return notFinishedCommands;
	}
	
	public void setNotFinishedCommands(List<EmulationCommand> notFinishedCommands) {
		this.notFinishedCommands = notFinishedCommands;
	}
	
	public void addFinishedCommand(EmulationCommand cmd) {
		finishedCommands.add(cmd);
	}
	
	public void addNotFinishedCommand(EmulationCommand cmd) {
		notFinishedCommands.add(cmd);
	}

}
