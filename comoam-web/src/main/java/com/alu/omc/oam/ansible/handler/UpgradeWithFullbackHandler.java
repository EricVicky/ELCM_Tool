package com.alu.omc.oam.ansible.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.config.UpgradeFullBackupConfig;
import com.alu.omc.oam.log.ParseResult;
import com.alu.omc.oam.log.TaskErrorHandler;


@Component("UPGRADE_FULLBACKUP_KVM_HANDLER")
@Scope(value = "prototype")
public class UpgradeWithFullbackHandler extends DefaultHandler 
{
    final static String   UPGRAGE_DONE_KEYWORD = "Start Upgrade";
    final static String   DESTROY_START_KEYWORD = "Destroy VM";
    boolean full_backup_done = false;
    boolean destroyed = false;
    private static Logger log = LoggerFactory.getLogger(UpgradeWithFullbackHandler.class);
    @Override
    public void onStart()
    {
    	super.onStart();
    	log.info("upgrade start on KVM");
    }

    @Override
    public void onSucceed()
    {
       log.info("upgrade on KVM succeed");
        COMStack stack = new COMStack(((UpgradeFullBackupConfig<KVMCOMConfig>)config).getConfig());
        service.update(stack);
    }
    @Override
    public void onError()
    {
        log.error("upgrade on KVM failed");
        sendErrorHandler();
    }

    private void sendErrorHandler()
    {
        //send error handler to client
        if(destroyed){ // allow user to do full restore if failed after destroy step 
           sender.send(getFulltopic(), new TaskErrorHandler<COMConfig>(Action.FULLRESTORE, this.getConfig().getEnvironment(), this.getConfig()));
        }else if(this.full_backup_done){// allow user to do start COM
            sender.send(getFulltopic(), new TaskErrorHandler<COMConfig>(Action.HEALING, this.getConfig().getEnvironment(), this.getConfig()));
            this.setSucceed(false);
        }
    }
    
   
    
    public void Parse(String log) {
        ParseResult pr = logParser.parse(log);
        sender.send(getFulltopic(), pr);
        if(pr.getStep() == null)
            return;
        if(pr.getStep().equalsIgnoreCase(UPGRAGE_DONE_KEYWORD)){
            full_backup_done = true;
        }else{
            pr.getStep().equalsIgnoreCase(DESTROY_START_KEYWORD);
            destroyed = true;
        }
    }
    
    
    @Override
	public ActionResult getActionResult(){
		if(this.succeed){
			return ActionResult.UPGRADE_SUCCEED;
		}else{
			return ActionResult.UPGRADE_FAIL;
		}
	}

	@Override
	public Action getAction() {
		return Action.UPGRADE;
	}
}