package com.alu.omc.oam.rest.kvm.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alu.omc.oam.ansible.AnsibleDelegator;
import com.alu.omc.oam.ansible.Ansibleworkspace;
import com.alu.omc.oam.ansible.persistence.JsonDataSource;
import com.alu.omc.oam.ansible.validation.ValidationResult;
import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.AnsibleLog;
import com.alu.omc.oam.config.ArsCOMConfig;
import com.alu.omc.oam.config.ArsOSCOMConfig;
import com.alu.omc.oam.config.AtcCOMConfig;
import com.alu.omc.oam.config.AtcOSCOMConfig;
import com.alu.omc.oam.config.BACKUPConfig;
import com.alu.omc.oam.config.CHKVMHostnameConfig;
import com.alu.omc.oam.config.CHKVMQosacCOMConfig;
import com.alu.omc.oam.config.CHOSHostnameConfig;
import com.alu.omc.oam.config.CHOSQosacCOMConfig;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.EncryPassword;
import com.alu.omc.oam.config.FullBackupConfig;
import com.alu.omc.oam.config.GRInstallConfig;
import com.alu.omc.oam.config.GRUnInstallConfig;
import com.alu.omc.oam.config.HpsimCOMConfig;
import com.alu.omc.oam.config.HpsimOSCOMConfig;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.config.NIC;
import com.alu.omc.oam.config.OSCOMConfig;
import com.alu.omc.oam.config.OperationLog;
import com.alu.omc.oam.config.QosacCOMConfig;
import com.alu.omc.oam.config.QosacOSCOMConfig;
import com.alu.omc.oam.config.UpgradeFullBackupConfig;
import com.alu.omc.oam.config.VMConfig;
import com.alu.omc.oam.kvm.model.Host;
import com.alu.omc.oam.service.COMStackService;
import com.alu.omc.oam.service.COMValidationService;
import com.alu.omc.oam.service.HostService;
import com.alu.omc.oam.service.OperationLogService;
import com.alu.omc.oam.util.EncryptUtils;
import com.alu.omc.oam.util.Json2Object;

@RestController 
public class CloudDeployController 
{
    @Resource
    private AnsibleDelegator ansibleDelegator;
    @Resource
    private Ansibleworkspace ansibleWorkspace;
    @Resource
    private HostService hostService;
    @Resource
    COMStackService cOMStackService;
    @Resource
    JsonDataSource jsonDataSource;
    @Resource
    OperationLogService operationLogService;
    @Resource
    COMValidationService cOMValidationService;
    
    @RequestMapping(value="/kvm/addipv6", method=RequestMethod.GET)
    public ValidationResult  repliacateData(@ModelAttribute("stackName") String stackName,@ModelAttribute("ipaddress") String ipaddress,
    		                                @ModelAttribute("gateway") String gateway,@ModelAttribute("prefix") String prefix) 
    {
    	ValidationResult res = new ValidationResult();
    	KVMCOMConfig config = getKVMCOMConfig(stackName);
    	Map<String, VMConfig> vmconfigs = config.getVm_config();
    	Iterator<String> iterator = vmconfigs.keySet().iterator();
    	while(iterator.hasNext()){
    		String vnfc = iterator.next();
    		VMConfig vmConfig = vmconfigs.get(vnfc);
			String vmIP = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
			cOMValidationService.setIp(vmIP);
			String checkRes= cOMValidationService.addIpv6();
			res.addWarningMes(checkRes);
    	}
    	String hostIp = config.getHost().getIp_address();
    	cOMValidationService.setIp(hostIp);
		String checkRes= cOMValidationService.grReplicateData("grReplicateData.sh");

    	return res;    			
    }
    
    @RequestMapping(value="/os/deployment", method=RequestMethod.POST)
    public void deploy( @RequestBody OSCOMConfig config) throws IOException, InterruptedException
    {
    	ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
        encryPassword(config);
        
    }
    @RequestMapping(value="/kvm/deployment", method=RequestMethod.POST)
    public void deploy( @RequestBody KVMCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
        encryPassword(config);
    }
    
    @RequestMapping(value="/kvm/healing", method=RequestMethod.POST)
    public void healing( @RequestBody KVMCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.HEALING, config );
        encryPassword(config);
    }
    
    private void encryPassword(EncryPassword ep){ 
        ep.setRoot_password(EncryptUtils.encryptPasswd(ep.getRoot_password()));
        ep.setAxadmin_password(EncryptUtils.encryptPasswd(ep.getAxadmin_password()));
    }

    @RequestMapping(value="/ovm/HPSIMdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody HpsimCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
    
    @RequestMapping(value="/os/ovm/HPSIMdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody HpsimOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
    
    @RequestMapping(value="/os/ovm/QOSACdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody QosacOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
        encryPassword(config);
    }
    
    @RequestMapping(value="/os/ovm/ARSdeployment", method=RequestMethod.POST)
    public void deployos( @RequestBody ArsOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
    
    @RequestMapping(value="/os/ovm/ATCdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody AtcOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
    
    @RequestMapping(value="/os/ovm/QOSACdelete", method=RequestMethod.POST)
    public void delete( @RequestBody QosacOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/os/ovm/HPSIMdelete", method=RequestMethod.POST)
    public void delete( @RequestBody HpsimOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/os/ovm/ATCdelete", method=RequestMethod.POST)
    public void delete( @RequestBody AtcOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/ovm/ATCdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody AtcCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
    
    @RequestMapping(value="/ovm/QOSACdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody QosacCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
        encryPassword(config);
    }
    
    @RequestMapping(value="/ovm/ARSdeployment", method=RequestMethod.POST)
    public void deploy( @RequestBody ArsCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.INSTALL, config );
    }
 

    @RequestMapping(value="/ovm/HPSIMupgrade", method=RequestMethod.POST)
    public void upgrade( @RequestBody HpsimCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.UPGRADE, config );
    }

    @RequestMapping(value="/ovm/QOSACupgrade", method=RequestMethod.POST)
    public void upgrade( @RequestBody QosacCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.UPGRADE, config );
    }
    
    @RequestMapping(value="/os/ovm/QOSACupgrade", method=RequestMethod.POST)
    public void upgrade( @RequestBody QosacOSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.UPGRADE, config );
    }
    
    @RequestMapping(value="/kvm/instances/QOSAC{name}", method=RequestMethod.POST)
    public void deleteKVM( @RequestBody QosacCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/kvm/instances/HPSIM{name}", method=RequestMethod.POST)
    public void deleteKVM( @RequestBody HpsimCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }

    @RequestMapping(value="/kvm/instances/ATC{name}", method=RequestMethod.POST)
    public void deleteKVM( @RequestBody AtcCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/kvm/instances/{name}", method=RequestMethod.POST)
    public void deleteKVM( @RequestBody KVMCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/os/instances/{name}", method=RequestMethod.POST)
    public void deleteOS( @RequestBody OSCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.DELETE, config );
    }
    
    @RequestMapping(value="/kvm/images", method=RequestMethod.GET)
    public List<String> images(@ModelAttribute("host") String host, @ModelAttribute("dir") String dir)  
    {
        try
        {
            return hostService.imagelist(host,"root", dir );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @RequestMapping(value="/kvm/upgrade", method=RequestMethod.POST)
    public void upgrade( @RequestBody KVMCOMConfig config) throws IOException, InterruptedException
    {
       //the request is from jmeter
       if(config.getVm_config() == null){
          KVMCOMConfig curConfig = getKVMCOMConfig(config.getStackName());
          curConfig.setOam_cm_image(config.getOam_cm_image());
          curConfig.setDb_image(config.getDb_image());
          config = curConfig;
        }
        ansibleDelegator.addAnsibleTask(Action.UPGRADE, config );
    }
    
    
    @RequestMapping(value="/kvm/upgradefullbackup", method=RequestMethod.POST)
    public void upgradeWithFullBackup( @RequestBody UpgradeFullBackupConfig<KVMCOMConfig> config) throws IOException, InterruptedException
    {
       ansibleDelegator.addAnsibleTask(Action.UPGRADE_FULLBACKUP, config );
    }
    
    @RequestMapping(value="/kvm/chhostname", method=RequestMethod.POST)
    public void kvmchhostname( @RequestBody CHKVMHostnameConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.CHHOSTNAME, config );
    }
    
    @RequestMapping(value="/kvm/qosac/chhostname", method=RequestMethod.POST)
    public void kvmqosacchhostname( @RequestBody CHKVMQosacCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.CHHOSTNAME, config );
    }
    
    @RequestMapping(value="/os/chhostname", method=RequestMethod.POST)
    public void oschhostname( @RequestBody CHOSHostnameConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.CHHOSTNAME, config );
    }
    
    @RequestMapping(value="/os/qosac/chhostname", method=RequestMethod.POST)
    public void osqosacchhostname( @RequestBody CHOSQosacCOMConfig config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.CHHOSTNAME, config );
    }
    
    @RequestMapping(value="/os/upgrade", method=RequestMethod.POST)
    public void osupgrade( @RequestBody OSCOMConfig config) throws IOException, InterruptedException
    {
       //the request is from jmeter
       if(config.getVm_config() == null){
          OSCOMConfig curConfig = getOSCOMConfig(config.getStackName());
          curConfig.setOam_cm_image(config.getOam_cm_image());
          curConfig.setDb_image(config.getDb_image());
          config = curConfig;
        }
        ansibleDelegator.addAnsibleTask(Action.UPGRADE, config );
    }
    
    @RequestMapping(value="/kvm/backup", method=RequestMethod.POST)
    public void kvmbackup( @RequestBody BACKUPConfig<KVMCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.BACKUP, config );
    }
    
    @RequestMapping(value="/kvm/qosacbackup", method=RequestMethod.POST)
    public void qosackvmbackup( @RequestBody BACKUPConfig<QosacCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.BACKUP, config );
    }
    @RequestMapping(value="/kvm/fullbackup", method=RequestMethod.POST)
    public void kvmfullbackup(@RequestBody FullBackupConfig<KVMCOMConfig> fullbackupconfig) throws Exception
    {
    	fullbackupconfig.setConfig(getKVMCOMConfig(fullbackupconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLBACKUP, fullbackupconfig );
    }
    @RequestMapping(value="/kvm/fullrestore", method=RequestMethod.POST)
    public void kvmfullrestore(@RequestBody FullBackupConfig<KVMCOMConfig> fullrestoreconfig) throws Exception
    {
    	fullrestoreconfig.setConfig(getKVMCOMConfig(fullrestoreconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLRESTORE, fullrestoreconfig );
    }
    
    @RequestMapping(value="/kvm/atc/fullbackup", method=RequestMethod.POST)
    public void kvmatcfullbackup(@RequestBody FullBackupConfig<AtcCOMConfig> fullbackupconfig) throws Exception
    {
    	fullbackupconfig.setConfig(getKVMATCCOMConfig(fullbackupconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLBACKUP, fullbackupconfig );
    }
    @RequestMapping(value="/kvm/atc/fullrestore", method=RequestMethod.POST)
    public void kvmatcfullrestore(@RequestBody FullBackupConfig<AtcCOMConfig> fullrestoreconfig) throws Exception
    {
    	fullrestoreconfig.setConfig(getKVMATCCOMConfig(fullrestoreconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLRESTORE, fullrestoreconfig );
    }
    
    @RequestMapping(value="/kvm/qosac/fullbackup", method=RequestMethod.POST)
    public void kvmqosacfullbackup(@RequestBody FullBackupConfig<QosacCOMConfig> fullbackupconfig) throws Exception
    {
    	fullbackupconfig.setConfig(getKVMQOSACCOMConfig(fullbackupconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLBACKUP, fullbackupconfig );
    }
    @RequestMapping(value="/kvm/qosac/fullrestore", method=RequestMethod.POST)
    public void kvmqosacfullrestore(@RequestBody FullBackupConfig<QosacCOMConfig> fullrestoreconfig) throws Exception
    {
    	fullrestoreconfig.setConfig(getKVMQOSACCOMConfig(fullrestoreconfig.getStackName()));
        ansibleDelegator.addAnsibleTask(Action.FULLRESTORE, fullrestoreconfig );
    }
    
    private QosacCOMConfig getKVMQOSACCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
         @SuppressWarnings("unchecked") 
         QosacCOMConfig config = new Json2Object<QosacCOMConfig>(){}.toMap(comStack.getComConfig());
         return config;
    }
    
    private AtcCOMConfig getKVMATCCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
         @SuppressWarnings("unchecked") 
         AtcCOMConfig config = new Json2Object<AtcCOMConfig>(){}.toMap(comStack.getComConfig());
         return config;
    }
    
    private KVMCOMConfig getKVMCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
         @SuppressWarnings("unchecked") 
         KVMCOMConfig config = new Json2Object<KVMCOMConfig>(){}.toMap(comStack.getComConfig());
         return config;
    }
     private OSCOMConfig getOSCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
         @SuppressWarnings("unchecked") 
         OSCOMConfig config = new Json2Object<OSCOMConfig>(){}.toMap(comStack.getComConfig());
         return config;
    }   
    
    @RequestMapping(value="/kvm/restore", method=RequestMethod.POST)
    public void kvmrestore( @RequestBody BACKUPConfig<KVMCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.RESTORE, config );
    }
    
    @RequestMapping(value="/kvm/qosacrestore", method=RequestMethod.POST)
    public void kvmqosacrestore( @RequestBody BACKUPConfig<QosacCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.RESTORE, config );
    }

    @RequestMapping(value="/kvm/instances", method=RequestMethod.GET)
    public List<COMStack>  kvminstances() throws IOException, InterruptedException
    {
    	List<COMStack> instances = cOMStackService.list();
    	return instances;
    }
    
    @RequestMapping(value="/operationlog", method=RequestMethod.GET)
    public List<OperationLog>  operationLog(@ModelAttribute("name") String name) throws IOException, InterruptedException
    {
    	List<OperationLog> operations = operationLogService.listLog(name);
    	return operations;
    }
    
    @RequestMapping(value="/ansiblelog", method=RequestMethod.GET)
    public AnsibleLog ansibleLog(@ModelAttribute("dir") String dir) throws Exception
    {
    	AnsibleLog AllLogs = new AnsibleLog(ansibleWorkspace.getWorkDirRoot().concat(dir));
    	return AllLogs;
    }
    
    @RequestMapping(value="/instances", method=RequestMethod.GET)
    public List<COMStack>  allinstances() throws IOException, InterruptedException
    {
    	List<COMStack> instances = cOMStackService.list();
    	return instances;
    }
    @RequestMapping(value="/gr/kvm/install", method=RequestMethod.POST)
    public void install_gr(@RequestBody GRInstallConfig<KVMCOMConfig> config) 
    {
       //the request is from jmeter
        if(config.getPri().getVm_config() == null){
            config.setPri(getKVMCOMConfig(config.getPri().getStackName()));
            config.setSec(getKVMCOMConfig(config.getSec().getStackName()));
        }
    	if(config.getGr_install_active()==true){
    		ansibleDelegator.addAnsibleTask(Action.GRINST_PRI, config);
    	}else{
    		ansibleDelegator.addAnsibleTask(Action.GRINST_SEC, config);
    	}
    }
    
    
        @RequestMapping(value="/gr/os/install", method=RequestMethod.POST)
    public void install_os_gr(@RequestBody GRInstallConfig<OSCOMConfig> config) 
    {
       //the request is from jmeter
        if(config.getPri().getVm_config() == null){
            config.setPri(getOSCOMConfig(config.getPri().getStackName()));
            config.setSec(getOSCOMConfig(config.getSec().getStackName()));
        }
    	if(config.getGr_install_active()==true){
    		ansibleDelegator.addAnsibleTask(Action.GRINST_PRI, config);
    	}else{
    		ansibleDelegator.addAnsibleTask(Action.GRINST_SEC, config);
    	}
    }  
    
    @RequestMapping(value="/gr/kvm/uninstall", method=RequestMethod.POST)
    public void uninstall_kvm_gr(@RequestBody GRUnInstallConfig<KVMCOMConfig> config) throws IOException, InterruptedException
    {
       //the request is from jmeter
       if(config.getComConfig().getVm_config() == null){
            config.setComConfig(getKVMCOMConfig(config.getStackName()));
        }
        COMStack comStack = cOMStackService.get(config.getStackName());
        COMStack mateStack = cOMStackService.get(comStack.getMate());  
        KVMCOMConfig mateCOMConfig = new Json2Object<KVMCOMConfig>(){}.toMap(mateStack.getComConfig());
        VMConfig vmconfig = mateCOMConfig.getVm_config().get("oam");
        String secoamIP = vmconfig.getNic().get(0).getIp_v4().getIpaddress();
        config.secOAMIP(secoamIP);
        ansibleDelegator.addAnsibleTask(Action.GRUNINST, config);
    } 
    
    @RequestMapping(value="/gr/os/uninstall", method=RequestMethod.POST)
    public void uninstall_os_gr(@RequestBody GRUnInstallConfig<OSCOMConfig> config) throws IOException, InterruptedException
    {
       //the request is from jmeter
        if(config.getComConfig().getVm_config() == null){
            config.setComConfig(getOSCOMConfig(config.getStackName()));
        }
        COMStack comStack = cOMStackService.get(config.getDeployment_prefix());
        COMStack mateStack = cOMStackService.get(comStack.getMate());
        OSCOMConfig mateCOMConfig = new Json2Object<OSCOMConfig>(){}.toMap(mateStack.getComConfig());
        NIC nic = mateCOMConfig.allInterface().get("oam").get(0);
        String secoamIP = nic.getIp_v4().getIpaddress();
        config.secOAMIP(secoamIP);
        ansibleDelegator.addAnsibleTask(Action.GRUNINST, config);
    } 
    
    @RequestMapping(value="/os/instances", method=RequestMethod.GET)
    public List<COMStack>  osinstances() throws IOException, InterruptedException
    {
    	List<COMStack> instances = cOMStackService.list();
    	return instances;
    }

    
    @RequestMapping(value="/kvm/hostips", method=RequestMethod.GET)
    public List<Host>  achostips() throws IOException, InterruptedException
    {
    	List<Host> achostips = hostService.hostIPs();
    	return achostips;
    }
    
    @RequestMapping(value="/os/backup", method=RequestMethod.POST)
    public void osbackup( @RequestBody BACKUPConfig<OSCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.BACKUP, config );
    }

    @RequestMapping(value="/os/restore", method=RequestMethod.POST)
    public void osrestore( @RequestBody BACKUPConfig<OSCOMConfig> config) throws IOException, InterruptedException
    {
        ansibleDelegator.addAnsibleTask(Action.RESTORE, config );
    }
    @RequestMapping(value="/ansible/task", method=RequestMethod.GET)
    public void executetask( @ModelAttribute("comStack") String comStack) 
    {
        ansibleDelegator.execute(comStack);
    }
}
