package com.alu.omc.oam.ansible;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.kvm.model.StackStatus;

@Component
public class RunningComstackLock
{
    public Map<String, Action> lockMap = new HashMap<String, Action>();

    public void lock(String comstack, Action action)
    {
        lockMap.put(comstack, action);
    }

    public boolean islocked(String comstack)
    {
        return lockMap.containsKey(comstack);
    }
    
    public void unlock(String comstack){
        lockMap.remove(comstack);
    }
    
    public Action getAction(String comstack){
    	return lockMap.get(comstack);
    }
    
    public StackStatus getStatus(String comstack){
        StackStatus status = new StackStatus();
        status.setLastAction(lockMap.get(comstack));
        if(status.getLastAction() != null){
            status.setState(StackStatus.IN_ACTION);
        }
        return status;
    }
}
