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

@Component("HEALING_KVM_HANDLER")
@Scope(value = "prototype")
public class HealingKVMHandler extends DefaultHandler{
	
	private static Logger log = LoggerFactory.getLogger(HealingKVMHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("healing start on KVM");
    }

    @Override
    public void onSucceed()
    {
        log.info("healing succeeded on KVM");
        
    }

    @Override
    public void onError()
    {
    	log.error("healing failed on KVM");
    }
    
    @Override
    public ActionResult getActionResult(){
        if(this.succeed){
            return ActionResult.HEALING_SUCCEED;
        }else{
            return ActionResult.HEALING_FAIL;
        }
    }

	@Override
	public Action getAction() {
		return Action.HEALING;
	}
}
