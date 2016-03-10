package com.alu.omc.oam.ansible.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.log.ParseResult;

@Component("REMOVEIPV6_KVM_HANDLER")
@Scope(value = "prototype")
public class Removeipv6KVMHandler extends DefaultHandler{
	
	private static Logger log = LoggerFactory.getLogger(Removeipv6KVMHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("removeipv6 start on KVM");
    }

    @Override
    public void onSucceed()
    {
        log.info("removeipv6 succeeded on KVM");
        COMStack stack = new COMStack(config);
        service.update(stack);
    }

    @Override
    public void onError()
    {
    	log.error("removeipv6 failed on KVM");
    }
    
    @Override
    public ActionResult getActionResult(){
        if(this.succeed){
            return ActionResult.REMOVEIPV6_SUCCEED;
        }else{
            return ActionResult.REMOVEIPV6_FAIL;
        }
    }

	@Override
	public Action getAction() {
		return Action.REMOVEIPV6;
	}
}
