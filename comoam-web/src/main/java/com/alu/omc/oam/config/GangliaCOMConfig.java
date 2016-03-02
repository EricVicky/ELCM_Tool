package com.alu.omc.oam.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GangliaCOMConfig extends OVMCOMConfig {

	@Override
	public COMType getCOMType() {
		return COMType.GANGLIA;
	}

}
