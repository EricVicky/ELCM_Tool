package com.alu.omc.oam.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alu.omc.oam.ansible.Inventory;

public class ADDPORTConfig extends COMConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private KVMCOMConfig config;
	private VMConfig vm_config;
	
	public KVMCOMConfig getConfig() {
		return config;
	}
	public void setConfig(KVMCOMConfig config) {
		this.config = config;
	}
	public VMConfig getVm_config() {
		return vm_config;
	}
	public void setVm_config(VMConfig vm_config) {
		this.vm_config = vm_config;
	}
	@Override
	public Inventory getInventory() {
		return config.getInventory();
	}
	@Override
	public String getVars() {
		return null;
	}
	@Override
	public Environment getEnvironment() {
		return Environment.KVM;
	}
	@Override
	public COMType getCOMType() {
		return config.getCOMType();
	}
	@Override
	public String getStackName() {
		return config.getStackName();
	}

	
	
}
