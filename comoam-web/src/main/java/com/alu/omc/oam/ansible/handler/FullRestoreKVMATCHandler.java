package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;

@Component("FULLRESTORE_KVM_ATC_HANDLER")
@Scope(value = "prototype")
public class FullRestoreKVMATCHandler extends DefaultHandler {

	private static Logger log = LoggerFactory.getLogger(FullRestoreKVMATCHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("Start fullrestore on ATC");
    }   

    @Override
    public void onSucceed()
    {
        log.info("fullrestore succeeded on ATC");     
    }

   
    @Override
    public void onError()
    {
    	log.error("fullrestore failed on ATC");
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

