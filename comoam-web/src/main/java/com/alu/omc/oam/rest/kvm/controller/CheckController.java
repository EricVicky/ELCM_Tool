package com.alu.omc.oam.rest.kvm.controller;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alu.omc.oam.ansible.RunningComstackLock;
import com.alu.omc.oam.ansible.validation.ValidationResult;
import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionResult;
import com.alu.omc.oam.config.AtcCOMConfig;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.FullBackupConfig;
import com.alu.omc.oam.config.GRInstallConfig;
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
    
    @RequestMapping(value="/check/grStatus", method=RequestMethod.GET)
    public ValidationResult  status(@ModelAttribute("stackName") String stackName) 
    {
    	ValidationResult res = new ValidationResult();
    	res.setSucceed(true);
    	if(runningComstackLock.getAction(stackName) == Action.GRINST_PRI || 
    	   runningComstackLock.getAction(stackName) == Action.GRINST_SEC){
    		res.setMessage("GR installlation in progress");
    	}else{
    		List<COMStack> stacks =  cOMStackService.list();
            for(COMStack stack : stacks){
            	if(stack.getName().equals(stackName)){
            		switch(stack.getActionResult()){  // NOSONAR
            		case GRINSTALL_SUCCEED:
            			res.setMessage("GR installation succeed");
            			break;
            		case GRINSTALL_FAIL:
            			res.setSucceed(false);
                        res.setMessage("GR installation failed");
                        break;
                    default:
                    	res.setSucceed(false);
                 	    res.setMessage("GR not installed");
            		}
            		break;
            	}
            }
    	}
    	return res;    			
    }
    
    @RequestMapping(value="/check/grReplicat_data", method=RequestMethod.GET)
    public ValidationResult  repliacateData(@ModelAttribute("stackName") String stackName) 
    {
    	ValidationResult res = new ValidationResult();
    	GRInstallConfig<KVMCOMConfig> config = new GRInstallConfig<>();
    	config.setPri(getKVMCOMConfig(stackName));
    	Map<String, VMConfig> vmconfigs = config.getPri().getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		if(("oam").equals(vnfc)){
    			VMConfig vmConfig = vmconfigs.get(vnfc);
    			String oamIP = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
    			cOMValidationService.setIp(oamIP);
    			break;
    		}
    	}
    	String checkRes= cOMValidationService.grReplicateData();
    	res.setMessage(checkRes);
    	return res;    			
    }
    
    @RequestMapping(value="/check/ping", method=RequestMethod.GET)
    public ValidationResult  ping(@ModelAttribute("host") String host) 
    {
    	ValidationResult res = new ValidationResult();
    	if (host!=null)
    	{
    		res.setSucceed(!hostService.ping((String)host)); // NOSONAR
    		if (!res.isSucceed()){
    			res.setMessage("This IP is in use, please change!");
    		}	   		
    	}
    	return res;    			
    }
    
    @RequestMapping(value="/check/cpuVTCheck", method=RequestMethod.GET)
    public ValidationResult  vTcheck(@ModelAttribute("hostip") String hostip) 
    {
    	ValidationResult res = new ValidationResult();
    	cOMValidationService.setIp(hostip);
    	String checkRes = cOMValidationService.cpuVTCheck(hostip);
    	if(isSucceed(checkRes)){
		    res.setSucceed(true);
    	}else{
    		res.setSucceed(false);
    	}	
    	return res; 
    }
    
    @RequestMapping(value="/kvm/check/unique", method=RequestMethod.GET)
    public ValidationResult uniqueCOM(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       if (name!=null && stacks != null && !stacks.isEmpty())
        {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)){
            	   res.setSucceed(false);
                   break;
               }
            }
        }
       return res;
    }
    
    @RequestMapping(value="/gr/kvm/checkinstalled", method=RequestMethod.GET)
    public ValidationResult grCheck(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       res.setSucceed(false);
       if (name!=null && stacks != null && !stacks.isEmpty()) {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)&&stack.getStatus()==Status.GRINSTALLED){
            	   res.setSucceed(true);
                   break;
               }
            }
        }
       return res;
    }
    
    @RequestMapping(value="/os/check/unique", method=RequestMethod.GET)
    public ValidationResult uniqueSTACK(@ModelAttribute("name") String name){
       List<COMStack> stacks =  cOMStackService.list();
       ValidationResult res = new ValidationResult();
       if (name!=null && stacks != null && !stacks.isEmpty())
        {
            for(COMStack stack : stacks){
               if (stack.getName().equals(name)){
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
        KVMCOMConfig comConfig = new Json2Object<KVMCOMConfig>(){}.toMap(comsStackStr);
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
    
    private boolean isSucceed(String res){
    	return res.contains("Success");
    }
    
    private boolean isWarning(String res){
    	return res.contains("Warning");
    }
    
    private KVMCOMConfig getKVMCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
        return new Json2Object<KVMCOMConfig>(){}.toMap(comStack.getComConfig());
    }
    
    @RequestMapping(value="/check/fullbackupPreCheck", method=RequestMethod.POST)
    public ValidationResult fullbackupCheckResult(@RequestBody FullBackupConfig<KVMCOMConfig> fullbackupconfig) {
    	fullbackupconfig.setConfig(getKVMCOMConfig(fullbackupconfig.getStackName()));
    	ValidationResult res = new ValidationResult();
    	StringBuilder hostname = new StringBuilder();
    	res.setSucceed(true);
    	String hostip = fullbackupconfig.getConfig().getHost().getIp_address();
    	Map<String, VMConfig> vmconfigs = fullbackupconfig.getConfig().getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		if(!("").equals(hostname.toString())){
    			hostname.append(":");
    		}
    		hostname.append(vmconfigs.get(vnfc).getHostname());
    	}
    	cOMValidationService.setIp(hostip);
    	String checkRes= cOMValidationService.fullbackupPreCheck(hostip,fullbackupconfig.getConfig().getVm_img_dir()+"/"+fullbackupconfig.getConfig().getDeployment_prefix(),
    			         hostname.toString(),fullbackupconfig.getRemote_server_ip(),fullbackupconfig.getRemote_server_dir());
    	if(!isSucceed(checkRes)){
    		res.setMessage(checkRes);
			res.setSucceed(false);
    	}else{
    		res.setMessage(checkRes);
    	}
    	return res;
    }
    
    @RequestMapping(value="/check/fullrestorePreCheck", method=RequestMethod.POST)
    public ValidationResult fullrestorePreCheck(@RequestBody FullBackupConfig<KVMCOMConfig> fullbackupconfig) {
    	fullbackupconfig.setConfig(getKVMCOMConfig(fullbackupconfig.getStackName()));
    	ValidationResult res = new ValidationResult();
    	StringBuilder hostname = new StringBuilder();
    	res.setSucceed(true);
    	String hostip = fullbackupconfig.getConfig().getHost().getIp_address();
    	Map<String, VMConfig> vmconfigs = fullbackupconfig.getConfig().getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		if(!("").equals(hostname.toString())){
    			hostname.append(":");
    		}
    		hostname.append(vmconfigs.get(vnfc).getHostname());
    	}
    	cOMValidationService.setIp(hostip);
    	String checkRes= cOMValidationService.fullrestorePreCheck(hostip,fullbackupconfig.getConfig().getVm_img_dir()+"/"+fullbackupconfig.getConfig().getDeployment_prefix(),
    			         hostname.toString(),fullbackupconfig.getRemote_server_ip(),fullbackupconfig.getRemote_server_dir());
    	if(!isSucceed(checkRes)){
    		res.setMessage(checkRes);
			res.setSucceed(false);
    	}else{
    		res.setMessage(checkRes);
    	}
    	return res;
    }
    
    @RequestMapping(value="/check/databackupPreCheck", method=RequestMethod.POST)
    public ValidationResult databackupCheckResult(@RequestBody BACKUPConfig<KVMCOMConfig> databackupconfig){
    	databackupconfig.setConfig(getKVMCOMConfig(databackupconfig.getStackName()));
    	ValidationResult res = new ValidationResult();
    	res.setSucceed(true);
    	Map<String, VMConfig> vmconfigs = databackupconfig.getConfig().getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		VMConfig vmConfig = vmconfigs.get(vnfc);
    		String vmIP = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
    		cOMValidationService.setIp(vmIP);
    		String filename = "^"+vmconfigs.get(vnfc).getHostname()+"_[A-Z]*_"+databackupconfig.getBackupLocation().getLocal_backup_file()+"$";
    		String checkRes = cOMValidationService.databackupPreCheck(databackupconfig.getBackupLocation().getLocal_backup_dir(),filename,
    				                         databackupconfig.getBackupLocation().getRemote_server_ip(),databackupconfig.getBackupLocation().getRemote_server_dir());
    		if(!isSucceed(checkRes)){
    			res.setMessage(vnfc+" : "+checkRes);
    			res.setSucceed(false);
    			break;
    		}else if(isWarning(checkRes)){
    			res.addWarningMes(checkRes);
    		}
    	}
    	return res;
    }
    
    @RequestMapping(value="/check/datarestorePreCheck", method=RequestMethod.POST)
    public ValidationResult datarestorePreCheck(@RequestBody BACKUPConfig<KVMCOMConfig> databackupconfig){
    	databackupconfig.setConfig(getKVMCOMConfig(databackupconfig.getStackName()));
    	ValidationResult res = new ValidationResult();
    	res.setSucceed(true);
    	Map<String, VMConfig> vmconfigs = databackupconfig.getConfig().getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		VMConfig vmConfig = vmconfigs.get(vnfc);
    		String vmIP = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
    		cOMValidationService.setIp(vmIP);
    		String checkRes = cOMValidationService.datarestorePreCheck(databackupconfig.getBackupLocation().getLocal_backup_dir(),databackupconfig.getBackupLocation().getLocal_backup_file(),
    				       vmconfigs.get(vnfc).getHostname(),databackupconfig.getBackupLocation().getRemote_server_ip(),databackupconfig.getBackupLocation().getRemote_server_dir());
    		if(!isSucceed(checkRes)){
    			res.setMessage(vnfc+" : "+checkRes);
    			res.setSucceed(false);
    			break;
    		}else if(isWarning(checkRes)){
    			res.addWarningMes(checkRes);
    		}
    	}
    	return res;
    }

}
