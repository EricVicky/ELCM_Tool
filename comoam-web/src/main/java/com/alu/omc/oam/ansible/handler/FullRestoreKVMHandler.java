package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.KVMCOMConfig;

@Component("FULLRESTORE_KVM_HANDLER")
@Scope(value = "prototype")
public class FullRestoreKVMHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(FullRestoreKVMHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start fullrestore on KVM");
    }   

    @Override
    public void onSucceed()
    {
        log.info("fullrestore succeeded on KVM");     
    }

   
    @Override
    public void onError()
    {
    	log.error("fullrestore failed on KVM");
    }

    @Override
    public ActionResult getActionResult(){
        if(this.succeed){
            return ActionResult.FULL_RESTORE_SUCCEED;
        }else{
            return ActionResult.FULL_RESTORE_FAIL;
        }
    }
    
	@Override
	public Action getAction() {
		return Action.FULLRESTORE;
	}
}

