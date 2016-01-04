package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.KVMCOMConfig;

@Component("BACKUP_KVM_HANDLER")
@Scope(value = "prototype")
public class BackupKVMHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(BackupKVMHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start backup on KVM");
    }   

    @Override
    public void onSucceed()
    {
        log.info("backup succeeded on KVM");     
    }

   
    @Override
    public void onError()
    {
    	log.error("backup failed on KVM");
    }
    
    @Override
	public ActionResult getActionResult(){
		if(this.succeed){
			return ActionResult.DATA_BACKUP_SUCCEED;
		}else{
			return ActionResult.DATA_BACKUP_FAIL;
		}
	}

	@Override
	public Action getAction() {
		return Action.BACKUP;
	}
}

