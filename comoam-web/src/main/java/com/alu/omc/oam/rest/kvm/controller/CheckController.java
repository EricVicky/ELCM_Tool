package com.alu.omc.oam.rest.kvm.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alu.omc.oam.ansible.RunningComstackLock;
import com.alu.omc.oam.ansible.validation.ValidationResult;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.config.Status;
import com.alu.omc.oam.config.VMConfig;
import com.alu.omc.oam.kvm.model.StackStatus;
import com.alu.omc.oam.service.COMStackService;
import com.alu.omc.oam.service.COMValidationService;
import com.alu.omc.oam.service.HostService;
import com.alu.omc.oam.util.Json2Object;

@RestController 
public class CheckController 
{

    @Resource
    private HostService hostService;
    @Resource
    COMStackService cOMStackService;
    @Resource
    RunningComstackLock runningComstackLock;
    @Resource
    COMValidationService cOMValidationService;
    
    @RequestMapping(value="/check/ping", method=RequestMethod.GET)
    public ValidationResult  ping(@ModelAttribute("host") String host) 
    {
    	ValidationResult res = new ValidationResult();
    	if (host!=null)
    	{
    		res.setSucceed(!hostService.ping((String)host));
    		if (!res.isSucceed()){
    			res.setMessage("This IP is in use, please change!");
    		}	   		
    	}
    	return res;    			
    }
    
    @RequestMapping(value="/kvm/check/unique", method=RequestMethod.GET)
    public ValidationResult uniqueCOM(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       if (name!=null && stacks != null && stacks.size() >0)
        {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)){
                   //unique = false;
            	   res.setSucceed(false);
                   break;
               }
            }
        }
       return res;
    }
    
    @RequestMapping(value="/check/backupPrecheck", method=RequestMethod.GET)
    public ValidationResult preCheck(@ModelAttribute("dir") String dir,@ModelAttribute("oamip") String oamip,
    		                         @ModelAttribute("dbip") String dbip,@ModelAttribute("cmip") String cmip){
    	ValidationResult res = new ValidationResult();
    	StringBuffer message = new StringBuffer();
    	cOMValidationService.setoamip(oamip);
    	String oam_checkRes = cOMValidationService.preCheckBeforeBackup(dir);
    	message.append(oam_checkRes + "\r\n");
    	cOMValidationService.setoamip(dbip);
    	String db_checkRes = cOMValidationService.preCheckBeforeBackup(dir);
    	message.append(db_checkRes + "\r\n");
    	cOMValidationService.setoamip(cmip);
    	String cm_checkRes = cOMValidationService.preCheckBeforeBackup(dir);
    	message.append(cm_checkRes + "\r\n");
        res.setMessage(message.toString());
        if((message.toString()).contains("Error")){
        	res.setSucceed(false);
        }else{
        	res.setSucceed(true);
        }
        return res;
    }
    
    @RequestMapping(value="/check/backupNfsPrecheck", method=RequestMethod.GET)
    public ValidationResult preNfsCheck(@ModelAttribute("dir") String dir,@ModelAttribute("nfsip") String nfsip,
    		                         @ModelAttribute("oamip") String oamip){
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setoamip(oamip);
    	String mntResult = cOMValidationService.mountNfsServer("/localbackup",dir,oamip,nfsip,"mount");
    	if((mntResult.contains("mount.nfs:"))){
    		res.setSucceed(false);
    		res.setMessage(mntResult);
    	}else{
    		String vnf_checkRes = cOMValidationService.preCheckBeforeBackup("/localbackup 30000");
    		res.setSucceed(true);
    		res.setMessage(vnf_checkRes);
    		cOMValidationService.mountNfsServer("/localbackup",dir,oamip,nfsip,"umount");
    	}
        return res;
    }
    
    @RequestMapping(value="/gr/kvm/checkinstalled", method=RequestMethod.GET)
    public ValidationResult GRCheck(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       if (name!=null && stacks != null && stacks.size() >0)
        {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)){
                   if(stack.getStatus()==Status.GRINSTALLED){
                	   res.setSucceed(true);
                       break;
                   }else{
                	   res.setSucceed(false);
                       break;
                   }
               }
            }
        }
       return res;
    }
    
    @RequestMapping(value="/os/check/unique", method=RequestMethod.GET)
    public ValidationResult uniqueSTACK(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       if (name!=null && stacks != null && stacks.size() >0)
        {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)){
                   //unique = false;
            	   res.setSucceed(false);
                   break;
               }
            }
        }
       return res;
    }

    @RequestMapping(value="/check/lockedhost", method=RequestMethod.GET)
    public ValidationResult  checkHostLocked(@ModelAttribute("stackName") String  stackName) 
    {
        ValidationResult res = new ValidationResult ();
        res.setSucceed(runningComstackLock.islocked(stackName));
        return res;
    }
    
    @RequestMapping(value="/check/host/status", method=RequestMethod.GET)
    public StackStatus  checkHostStatus(@ModelAttribute("stackName") String  stackName) 
    {
        return runningComstackLock.getStatus(stackName);
    }
    
    @RequestMapping(value="/kvm/checkcom", method=RequestMethod.GET)
    public ValidationResult checkCOM(@ModelAttribute("stackName") String  stackName) 
    {
        COMStack comStack = cOMStackService.get(stackName);
        if(comStack == null ||comStack.getActionResult() == null ){
            return new ValidationResult(false, "in progress");
        }
        if(comStack.getActionResult().name().toLowerCase().indexOf("fail") != -1){
            return new ValidationResult(false, "failed");
        }
        String comsStackStr = comStack.getComConfig();
        KVMCOMConfig comConfig = new Json2Object<KVMCOMConfig>().toMap(comsStackStr);
        VMConfig vmConfig =  comConfig.getVm_config().get("oam");
        String ipaddress = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
        boolean processUp = cOMValidationService.checkIfCOMProcessUp(ipaddress);
        ValidationResult rs = new ValidationResult();
        rs.setSucceed(processUp);
        if(processUp){
            rs.setMessage("all processes up");
        }else{
            rs.setMessage("in progress");
        }
        return rs;
    }


}
