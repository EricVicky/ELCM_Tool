package com.alu.omc.oam.config;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alu.omc.oam.ansible.Inventory;
import com.alu.omc.oam.util.Json2Object;
import com.alu.omc.oam.util.JsonYamlConverter;

public class ADDPORTConfig extends COMConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private KVMCOMConfig config;
	public Map<String, VMConfig> vm_config;
	
	public KVMCOMConfig getConfig() {
		return config;
	}
	public void setConfig(KVMCOMConfig config) {
		this.config = config;
	}
	public Map<String, VMConfig> getVm_config() {
		return vm_config;
	}
	public void setVm_config(Map<String, VMConfig> vm_config) {
		this.vm_config = vm_config;
	}
	@Override
	public Inventory getInventory() {
		return config.getInventory();
	}
	@Override
	public String getVars() {
        Iterator<String> it = vm_config.keySet().iterator(); 
	    while(it.hasNext()){
	        String name = it.next();
	        @SuppressWarnings("unchecked")
            VMConfig vmcfg = vm_config.get(name);
	    }
	   String json = Json2Object.object2Json(this);
       return JsonYamlConverter.convertJson2Yaml(json);
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
