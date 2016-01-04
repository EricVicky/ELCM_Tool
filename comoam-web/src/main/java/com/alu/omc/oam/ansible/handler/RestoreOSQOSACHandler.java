package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;

@Component("RESTORE_OPENSTACK_QOSAC_HANDLER")
@Scope(value = "prototype")
public class RestoreOSQOSACHandler extends DefaultHandler{
	
	private static Logger log = LoggerFactory.getLogger(RestoreOSQOSACHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("restore QOSAC start on OS");
    }
    
    @Override
    public void onSucceed()
    {
        log.info("restore QOSAC succeeded on OS");
        
    }

    @Override
    public void onError()
    {
    	log.error("restore QOSAC failed on OS");
    }

    @Override
    public ActionResult getActionResult(){
        if(this.succeed){
            return ActionResult.DATA_RESTORE_SUCCEED;
        }else{
            return ActionResult.DATA_RESTORE_FAIL;
        }
    }
    
	@Override
	public Action getAction() {
		// TODO Auto-generated method stub
		return Action.RESTORE;
	}
}
