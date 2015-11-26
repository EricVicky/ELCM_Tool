package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;

@Component("FULLRESTORE_KVM_QOSAC_HANDLER")
@Scope(value = "prototype")
public class FullRestoreKVMQOSACHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(FullRestoreKVMQOSACHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start fullrestore on QOSAC");
    }   

    @Override
    public void onSucceed()
    {
        log.info("fullrestore succeeded on QOSAC");     
    }

   
    @Override
    public void onError()
    {
    	log.error("fullrestore failed on QOSAC");
    }

	@Override
	public Action getAction() {
		return Action.FULLRESTORE;
	}
}

