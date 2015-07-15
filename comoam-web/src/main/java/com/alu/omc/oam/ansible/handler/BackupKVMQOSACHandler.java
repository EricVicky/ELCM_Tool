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

@Component("BACKUP_KVM_QOSAC_HANDLER")
@Scope(value = "prototype")
public class BackupKVMQOSACHandler extends DefaultHandler {

    private static Logger log = LoggerFactory.getLogger(BackupKVMQOSACHandler.class);
    @Override
    public void onStart()
    {
        runningComstackLock.lock(getKVMConfig().getStackName(), Action.BACKUP);
    }
    
    @SuppressWarnings("unchecked")
	private KVMCOMConfig getKVMConfig(){
    	return ((BACKUPConfig<KVMCOMConfig>)config).getConfig();
    }

    @Override
    public void onSucceed()
    {
        log.info("backup succeed");
        runningComstackLock.unlock(getKVMConfig().getStackName());
        
    }

    @Override
    public void onEnd()
    {
    	if(this.succeed){
        	this.onSucceed();
        	END.setResult(ParseResult.SUCCEED);
        }else{
        	END.setResult(ParseResult.FAILED);
            this.onError();
        }
        sender.send(getFulltopic(), END);
        runningComstackLock.unlock(getKVMConfig().getStackName());

    }
    public String getFulltopic(){
        return this.topic.concat(getKVMConfig().getHost().getIp_address());
     }
    @Override
    public void onError()
    {
        runningComstackLock.unlock(getKVMConfig().getStackName());
    }
}

