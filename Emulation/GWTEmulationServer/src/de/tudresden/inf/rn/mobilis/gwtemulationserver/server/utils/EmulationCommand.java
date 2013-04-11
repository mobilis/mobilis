package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EmulationCommands")
public class EmulationCommand implements Serializable {
	
	private Long id;
	private String appNamespace;
	private String command;
	private String sendTo;
	
	public EmulationCommand() {
		this.appNamespace = "";
		this.command = "";
		this.sendTo = "";
	}
	
	public EmulationCommand(String ns, String cmd, String sendTo) {
		this.appNamespace = ns;
		this.command = cmd;
		this.sendTo = sendTo;
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
	public String getAppNamespace() {
		return appNamespace;
	}
	public void setAppNamespace(String appNamespace) {
		this.appNamespace = appNamespace;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

}
