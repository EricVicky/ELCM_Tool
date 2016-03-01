package com.alu.omc.oam.ansible.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.GangliaOSCOMConfig;
import com.alu.omc.oam.config.QosacCOMConfig;
import com.alu.omc.oam.config.QosacOSCOMConfig;

@Component("DELETE_OPENSTACK_GANGLIA_HANDLER")
@Scope(value = "prototype")
public class DeleteOSGANGLIAHandler extends DeleteOsHandler {
	public String getFulltopic() {
		GangliaOSCOMConfig cfg = (GangliaOSCOMConfig) config;
		return this.topic.concat(cfg.getStackName());
	}
}
