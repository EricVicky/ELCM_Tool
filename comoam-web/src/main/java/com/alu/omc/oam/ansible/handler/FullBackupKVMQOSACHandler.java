package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;

@Component("FULLBACKUP_KVM_QOSAC_HANDLER")
@Scope(value = "prototype")
public class FullBackupKVMQOSACHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(FullBackupKVMQOSACHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start fullbackup on QOSAC");
    }   

    @Override
    public void onSucceed()
    {
        log.info("fullbackup succeeded on QOSAC");     
    }

   
    @Override
    public void onError()
    {
    	log.error("fullbackup failed on QOSAC");
    }

	@Override
	public Action getAction() {
		return Action.FULLBACKUP;
	}
}

