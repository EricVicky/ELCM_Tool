package com.alu.omc.oam.ansible.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.GangliaCOMConfig;

@Component("DELETE_KVM_GANGLIA_HANDLER")
@Scope(value = "prototype")

public class DeleteKVMGANGLIAHandler extends DeleteKVMHandler {
	public String getFulltopic(){
		GangliaCOMConfig cfg = (GangliaCOMConfig)config;
	       return this.topic.concat(cfg.getStackName());
	}
}
