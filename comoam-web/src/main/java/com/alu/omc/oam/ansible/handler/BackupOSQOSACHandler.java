package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.log.ParseResult;

@Component("BACKUP_OPENSTACK_QOSAC_HANDLER")
@Scope(value = "prototype")
public class BackupOSQOSACHandler extends DefaultHandler {

    private static Logger log = LoggerFactory.getLogger(BackupOSQOSACHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start backup QOSAC on OS");
    }
 

    @Override
    public void onSucceed()
    {
        log.info("backup QOSAC succeed on OS");
        
    }

    @Override
    public void onError()
    {
    	log.error("backup QOSAC failed on openstack");
    }

	@Override
	public Action getAction() {
		// TODO Auto-generated method stub
		return Action.BACKUP;
	}

}

