package com.alu.omc.oam.rest.kvm.controller;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    
    @RequestMapping(value="/check/fullbackupPreCheck", method=RequestMethod.GET)
    public ValidationResult fullbackupCheckResult(@ModelAttribute("deployment_prefix") String deployment_prefix,@ModelAttribute("vm_img_dir") String vm_img_dir,
    		                                @ModelAttribute("remoteip") String remoteip,@ModelAttribute("remotedir") String remotedir,@ModelAttribute("hostip") String hostip){
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setIp(hostip);
    	String checkRes= cOMValidationService.fullbackupPreCheck(deployment_prefix,vm_img_dir,remoteip,remotedir);
    	if(checkRes.contains("Success")){
    		res.setSucceed(true);
    	}else{
    		res.setSucceed(false);
    	}
    	res.setMessage(checkRes);
    	return res;
    }
    
    @RequestMapping(value="/check/fullrestorePreCheck", method=RequestMethod.GET)
    public ValidationResult fullrestoreCheckResult(@ModelAttribute("deployment_prefix") String deployment_prefix,@ModelAttribute("vm_img_dir") String vm_img_dir,
    		                                @ModelAttribute("remoteip") String remoteip,@ModelAttribute("remotedir") String remotedir,@ModelAttribute("hostip") String hostip){
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setIp(hostip);
    	String checkRes= cOMValidationService.fullrestorePreCheck(deployment_prefix,vm_img_dir,remoteip,remotedir);
    	if(checkRes.contains("Success")){
    		res.setSucceed(true);
    	}else{
    		res.setSucceed(false);
    	}
    	res.setMessage(checkRes);
    	return res;
    }
    
    @RequestMapping(value="/check/databackupPreCheck", method=RequestMethod.GET)
    public ValidationResult databackupCheckResult(@ModelAttribute("localdir") String localdir,@ModelAttribute("filename") String filename,
    		                                      @ModelAttribute("remoteip") String remoteip,@ModelAttribute("remotedir") String remotedir,
    		                                      @ModelAttribute("oamip") String oamip,@ModelAttribute("dbip") String dbip,@ModelAttribute("cmip") String cmip){
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setIp(oamip);
    	String oam_checkRes = cOMValidationService.databackupPreCheck(localdir,filename,remoteip,remotedir);
    	String[] oam_message = Arrays.copyOfRange(oam_checkRes.split("\r\n"), 3, oam_checkRes.split("\r\n").length-1);
    	cOMValidationService.setIp(dbip);
    	String db_checkRes = cOMValidationService.databackupPreCheck(localdir,remoteip,filename,remotedir);
    	String[] db_message = Arrays.copyOfRange(db_checkRes.split("\r\n"), 3, db_checkRes.split("\r\n").length-1);
    	oam_message = Arrays.copyOf(oam_message, oam_message.length + db_message.length);
    	System.arraycopy(db_message, 0, oam_message, oam_message.length-db_message.length, db_message.length);
    	if(cmip == ""){
    		res.setMutiMessage(oam_message);	
    	}else{
    		cOMValidationService.setIp(cmip);
    		String cm_checkRes = cOMValidationService.databackupPreCheck(localdir,filename,remoteip,remotedir);
    		String[] cm_message = Arrays.copyOfRange(cm_checkRes.split("\r\n"), 3, cm_checkRes.split("\r\n").length-1);
    		oam_message = Arrays.copyOf(oam_message, oam_message.length + cm_message.length);
    		System.arraycopy(cm_message, 0, oam_message, oam_message.length-cm_message.length, cm_message.length);
    		res.setMutiMessage(oam_message);
    	}
    	for(int index=0;index<res.getMutiMessage().length;index++){
    		if(res.getMutiMessage()[index].contains("Success")){
    			res.setSucceed(true);
    		}else{
    			res.setSucceed(false);
    		}	
    	}
        return res;
    }
    
    @RequestMapping(value="/check/datarestorePreCheck", method=RequestMethod.GET)
    public ValidationResult datarestoreCheckResult(@ModelAttribute("localdir") String localdir,@ModelAttribute("filename") String filename,
    		                                      @ModelAttribute("remoteip") String remoteip,@ModelAttribute("remotedir") String remotedir,
    		                                      @ModelAttribute("oamip") String oamip,@ModelAttribute("dbip") String dbip,@ModelAttribute("cmip") String cmip){
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setIp(oamip);
    	String oam_checkRes = cOMValidationService.datarestorePreCheck(localdir,filename,remoteip,remotedir);
    	String[] oam_message = Arrays.copyOfRange(oam_checkRes.split("\r\n"), 3, oam_checkRes.split("\r\n").length-1);
    	cOMValidationService.setIp(dbip);
    	String db_checkRes = cOMValidationService.datarestorePreCheck(localdir,remoteip,filename,remotedir);
    	String[] db_message = Arrays.copyOfRange(db_checkRes.split("\r\n"), 3, db_checkRes.split("\r\n").length-1);
    	oam_message = Arrays.copyOf(oam_message, oam_message.length + db_message.length);
    	System.arraycopy(db_message, 0, oam_message, oam_message.length-db_message.length, db_message.length);
    	if(cmip == ""){
    		res.setMutiMessage(oam_message);	
    	}else{
    		cOMValidationService.setIp(cmip);
    		String cm_checkRes = cOMValidationService.datarestorePreCheck(localdir,filename,remoteip,remotedir);
    		String[] cm_message = Arrays.copyOfRange(cm_checkRes.split("\r\n"), 3, cm_checkRes.split("\r\n").length-1);
    		oam_message = Arrays.copyOf(oam_message, oam_message.length + cm_message.length);
    		System.arraycopy(cm_message, 0, oam_message, oam_message.length-cm_message.length, cm_message.length);
    		res.setMutiMessage(oam_message);
    	}
    	for(int index=0;index<res.getMutiMessage().length;index++){
    		if(res.getMutiMessage()[index].contains("Success")){
    			res.setSucceed(true);
    		}else{
    			res.setSucceed(false);
    		}	
    	}
        return res;
    }

}
