package com.alu.omc.oam.ansible.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.GRUnInstallConfig;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.config.UpgradeFullBackupConfig;
import com.alu.omc.oam.log.ParseResult;
import com.alu.omc.oam.log.TaskErrorHandler;


@Component("UPGRADE_FULLBACKUP_KVM_HANDLER")
@Scope(value = "prototype")
public class UpgradeWithFullbackHandler extends DefaultHandler 
{
    final static String   FULLBACKUP_DONE_KEYWORD = "Full Backup";
    final static String   DESTROY_START_KEYWORD = "Start Upgrade";
    final static String   GR_CHECK_KEYWORD = "Check GR Status";
    boolean full_backup_done = false;
    boolean destroyed = false;
    boolean gr_check = false;
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
            @SuppressWarnings("unchecked")
            UpgradeFullBackupConfig<KVMCOMConfig> upconfig =  (UpgradeFullBackupConfig<KVMCOMConfig>)this.getConfig();
            sender.send(getFulltopic(), new TaskErrorHandler<KVMCOMConfig>(Action.HEALING, this.getConfig().getEnvironment(), upconfig.getConfig()));
        }else if(this.gr_check){// allow user to uninstall GR
        	@SuppressWarnings("unchecked")
        	UpgradeFullBackupConfig<KVMCOMConfig> grconfig = (UpgradeFullBackupConfig<KVMCOMConfig>)this.getConfig();
        	sender.send(getFulltopic(), new TaskErrorHandler<KVMCOMConfig>(Action.GRUNINST, this.getConfig().getEnvironment(), grconfig.getConfig()));
        }
    }
    
   
    
    public void Parse(String log) {
        ParseResult pr = logParser.parse(log);
        sender.send(getFulltopic(), pr);
        if(pr.getStep() == null)
            return;
        if(pr.getStep().equalsIgnoreCase(FULLBACKUP_DONE_KEYWORD)){
            full_backup_done = true;
        }else if( pr.getStep().equalsIgnoreCase(DESTROY_START_KEYWORD)){
            destroyed = true;
        }else if( pr.getStep().equalsIgnoreCase(GR_CHECK_KEYWORD)){
        	gr_check = true;
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
