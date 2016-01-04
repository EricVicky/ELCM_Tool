package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.KVMCOMConfig;

@Component("FULLBACKUP_KVM_HANDLER")
@Scope(value = "prototype")
public class FullBackupKVMHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(FullBackupKVMHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start fullbackup on KVM");
    }   

    @Override
    public void onSucceed()
    {
        log.info("fullbackup succeeded on KVM");     
    }

   
    @Override
    public void onError()
    {
    	log.error("fullbackup failed on KVM");
    }
    
    @Override
    public ActionResult getActionResult(){
        if(this.succeed){
            return ActionResult.FULL_BACKUP_SUCCEED;
        }else{
            return ActionResult.FULL_BACKUP_FAIL;
        }
    }
    
	@Override
	public Action getAction() {
		return Action.FULLBACKUP;
	}
}

