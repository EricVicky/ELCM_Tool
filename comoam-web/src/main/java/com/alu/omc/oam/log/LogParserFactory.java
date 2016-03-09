package com.alu.omc.oam.log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionKey;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.COMType;
import com.alu.omc.oam.config.Environment;

@Component
public class LogParserFactory
{
    private final Map<ActionKey, ILogParser> parserCache = new HashMap<ActionKey, ILogParser>();
    
    public LogParserFactory(){
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM), kvmInstallParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.KVM), kvmUpgradeParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.OPENSTACK), osUpgradeParser());
        parserCache.put(new ActionKey(Action.BACKUP, Environment.KVM), kvmBackupParser());
        parserCache.put(new ActionKey(Action.RESTORE, Environment.KVM), kvmRestoreParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK), osInstallParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.KVM), kvmDeleteParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.OPENSTACK), osDeleteParser());
        parserCache.put(new ActionKey(Action.BACKUP, Environment.OPENSTACK), osBackupParser());
        parserCache.put(new ActionKey(Action.RESTORE, Environment.OPENSTACK), osRestoreParser());
        parserCache.put(new ActionKey(Action.GRINST_PRI, Environment.KVM), kvmGrInstPriParser());
        parserCache.put(new ActionKey(Action.GRINST_SEC, Environment.KVM), kvmGrInstSecParser());
        parserCache.put(new ActionKey(Action.GRUNINST, Environment.KVM), kvmGrUnInstParser());
        parserCache.put(new ActionKey(Action.GRINST_PRI, Environment.OPENSTACK), osGrInstPriParser());
        parserCache.put(new ActionKey(Action.GRINST_SEC, Environment.OPENSTACK), osGrInstSecParser());
        parserCache.put(new ActionKey(Action.GRUNINST, Environment.OPENSTACK), osGrUnInstParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM, COMType.HPSIM), kvmovmInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM, COMType.GANGLIA), kvmovmInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM, COMType.ATC), kvmovmInstallParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.KVM, COMType.ATC), kvmovmDeleteParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.KVM, COMType.ATC), kvmovmUpgradeParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM, COMType.QOSAC), kvmqosacInstallParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.KVM, COMType.QOSAC), kvmQosacDeleteParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.KVM, COMType.HPSIM), kvmovmUpgradeParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.KVM, COMType.GANGLIA), kvmovmUpgradeParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.KVM, COMType.HPSIM), kvmovmDeleteParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.KVM, COMType.GANGLIA), kvmovmDeleteParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.KVM, COMType.QOSAC), kvmqosacUpgradeParser());
        parserCache.put(new ActionKey(Action.BACKUP, Environment.KVM, COMType.QOSAC), kvmqosacBackupParser());
        parserCache.put(new ActionKey(Action.RESTORE, Environment.KVM, COMType.QOSAC), kvmqosacRestoreParser());
        parserCache.put(new ActionKey(Action.BACKUP, Environment.OPENSTACK, COMType.QOSAC), osqosacBackupParser());
        parserCache.put(new ActionKey(Action.RESTORE, Environment.OPENSTACK, COMType.QOSAC), osqosacRestoreParser());        
        parserCache.put(new ActionKey(Action.INSTALL, Environment.KVM, COMType.ARS), kvmarsInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK, COMType.QOSAC), osqosacInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK, COMType.HPSIM), oshpsimInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK, COMType.GANGLIA), osgangliaInstallParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK, COMType.ATC), osatcInstallParser());
        parserCache.put(new ActionKey(Action.UPGRADE, Environment.OPENSTACK, COMType.QOSAC), osqosacupgradeParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.OPENSTACK, COMType.QOSAC), osqosacdeleteParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.OPENSTACK, COMType.ATC), osatcdeleteParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.OPENSTACK, COMType.HPSIM), oshpsimdeleteParser());
        parserCache.put(new ActionKey(Action.DELETE, Environment.OPENSTACK, COMType.GANGLIA), osgangliadeleteParser());
        parserCache.put(new ActionKey(Action.INSTALL, Environment.OPENSTACK, COMType.ARS), osarsInstallParser());
        parserCache.put(new ActionKey(Action.CHHOSTNAME, Environment.KVM), kvmchhostnameParser());
        parserCache.put(new ActionKey(Action.CHHOSTNAME, Environment.KVM, COMType.QOSAC), kvmqosacchhostnameParser());
        parserCache.put(new ActionKey(Action.CHHOSTNAME, Environment.OPENSTACK), oschhostnameParser());
        parserCache.put(new ActionKey(Action.CHHOSTNAME, Environment.OPENSTACK, COMType.QOSAC), osqosacchhostnameParser());
        parserCache.put(new ActionKey(Action.FULLBACKUP, Environment.KVM), kvmfullBackupParser());
        parserCache.put(new ActionKey(Action.FULLRESTORE, Environment.KVM), kvmfullRestoreParser());
        parserCache.put(new ActionKey(Action.FULLBACKUP, Environment.KVM, COMType.ATC), kvmfullatcBackupParser());
        parserCache.put(new ActionKey(Action.FULLRESTORE, Environment.KVM, COMType.ATC), kvmfullatcRestoreParser());
        parserCache.put(new ActionKey(Action.FULLBACKUP, Environment.KVM, COMType.GANGLIA), kvmfullgangliaBackupParser());
        parserCache.put(new ActionKey(Action.FULLRESTORE, Environment.KVM, COMType.GANGLIA), kvmfullgangliaRestoreParser());
        parserCache.put(new ActionKey(Action.FULLBACKUP, Environment.KVM, COMType.QOSAC), kvmfullqosacBackupParser());
        parserCache.put(new ActionKey(Action.FULLRESTORE, Environment.KVM, COMType.QOSAC), kvmfullqosacRestoreParser());
        parserCache.put(new ActionKey(Action.UPGRADE_FULLBACKUP, Environment.KVM), kvmUpgradeFullbackParser());
        parserCache.put(new ActionKey(Action.HEALING, Environment.KVM), kvmHealingParser());
        parserCache.put(new ActionKey(Action.ADDIPV6, Environment.KVM), kvmAddipv6Parser());
    }
    
    private ILogParser kvmAddipv6Parser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[start\\_com\\s\\|\\sstart\\scom\\sapplication\\]", "Start COM");
        dict.put("TASK\\:\\s\\[add\\_ipv6\\s\\|\\sadd\\sipv6\\saddress\\]", "Adding Ipv6");
        dict.put("TASK\\:\\s\\[stop\\_com\\s\\|\\sstop\\scom\\sapplication\\]", "Stop COM");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmfullqosacBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_backup\\s\\|\\smkdir\\sto\\ssave\\ssnapshot\\sfor\\sVM\\]", "Full Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmfullqosacRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_restore\\s\\|\\smkdir\\sto\\scopy\\ssnapshot\\sfor\\sVM\\]", "Full Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmfullatcBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_backup\\s\\|\\smkdir\\sto\\ssave\\ssnapshot\\sfor\\sVM\\]", "Full Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmfullatcRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_restore\\s\\|\\smkdir\\sto\\scopy\\ssnapshot\\sfor\\sVM\\]", "Full Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmfullgangliaBackupParser() {
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_backup\\s\\|\\smkdir\\sto\\ssave\\ssnapshot\\sfor\\sVM\\]", "Full Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
        }
    private ILogParser kvmfullgangliaRestoreParser() {
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_full\\_restore\\s\\|\\smkdir\\sto\\scopy\\ssnapshot\\sfor\\sVM\\]", "Full Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
        } 
    private ILogParser oschhostnameParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[oam\\spre\\schange\\shostname\\]", "Changing Hostname");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osqosacchhostnameParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[oam\\spre\\schange\\shostname\\]", "Changing Hostname");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmchhostnameParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[oam\\spre\\schange\\shostname\\]", "Changing Hostname");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmqosacchhostnameParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[oam\\spre\\schange\\shostname\\]", "Changing Hostname");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmGrInstPriParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[install\\sPRI\\sOAM\\sGR\\]", "Pri GR Install");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmGrInstSecParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[install\\sSEC\\sDB\\sGR\\]", "Sec GR Install");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmGrUnInstParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[Uninstall\\sOAM\\sGR\\]", "GR Uninstall");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osGrInstPriParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[install\\sPRI\\sOAM\\sGR\\]", "Pri GR Install");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser osGrInstSecParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[install\\sSEC\\sDB\\sGR\\]", "Sec GR Install");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osGrUnInstParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[Uninstall\\sOAM\\sGR\\]", "GR Uninstall");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmDeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vms\\s\\|\\sundefine\\svirtual\\smachine\\]", "Undefine Virtual Machine");
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vms\\s\\|\\sdestroy\\svirtual\\smachine\\]", "Destroy Virtual Machine");
        dict.put("PLAY\\s\\[destroy\\sall\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osDeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\sdestroy\\sthe\\sstack\\]", "Destroy stack");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\scheck\\spresence\\sof\\sstack\\]", "Check Presence of stack");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[backup\\scom\\sdata\\]", "Data Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmfullBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[backup\\scom\\sdata\\]", "Full Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmfullRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[backup\\scom\\sdata\\]", "Full Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\screate\\slocal\\srestore\\sdirectory\\]", "Data Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser osBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[start\\_com\\s\\|\\sstart\\scom\\sapplication\\]", "Finished");
        dict.put("PLAY\\s\\[start\\scom\\sapplication\\]", "Data Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser osRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("TASK\\:\\s\\[start\\_com\\s\\|\\sstart\\scom\\sapplication\\]", "Finished");
        dict.put("PLAY\\s\\[start\\scom\\sapplication\\]", "Data Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[run\\spost\\sreplace\\sscript\\,\\smay\\stake\\saround\\s20\\sminutes\\]", "Start COM");
        dict.put("wait\\sfor\\svirtual\\smachines\\sto\\sbe\\salive", "Cloud Init");
        dict.put("TASK\\:\\s\\[deploy\\_stack\\s\\|\\scheck\\spresence\\sof\\sheat\\sstack\\]", "Check Presence of Heat Stack");
        dict.put("TASK\\:\\s\\[stack\\_templates\\s\\|\\sRunning\\swith\\sthe\\sfollowing\\soptions\\]", "Generate Heat Templates");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\svaliadtion\\skey\\]", "Valiadtion Key");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Start");
        return new LogParser(dict);
    }

	public  ILogParser getLogParser(Action action, COMConfig config)
    {
        try
        {
            return parserCache.get(new ActionKey(action, config.getEnvironment(), config.getCOMType())).clone();
        }
        catch (CloneNotSupportedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private ILogParser kvmInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("PLAY\\s\\[image\\sreplacement\\spost\\sscript\\]", "Prepare Install Options");
        dict.put("TASK\\:\\s\\[vnf\\_create\\_vms\\s\\|\\screate\\svirtual\\smachine\\sinstance\\]","Start VM Instance");
        dict.put("TASK\\:\\s\\[vnf\\_create\\_disk\\s\\|\\screate\\sdata\\sdisk\\simage\\]", "Generate Config Driver");
        dict.put("PLAY\\s\\[prepare\\sdata\\sfor\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
    }
    private ILogParser kvmUpgradeParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\srestore\\sdata\\]", "Data Restore");
        dict.put("TASK\\:\\s\\[upload\\_install\\_options\\s\\|\\supload\\sinstall\\_options\\]", "COM Upgrade");
        dict.put("TASK\\:\\s\\[vnf\\_create\\_vms\\s\\|\\scopy\\sqcow2\\sdisk\\simage\\]", "Post Image Replacement");
        dict.put("TASK\\:\\s\\[vnf\\_prepare\\_vms\\s\\|\\screate\\sdata\\sdirectory\\sfor\\svirtual\\smachine\\]","Prepare Virtual Machines");
        dict.put("TASK\\:\\s\\[backup\\_data\\s\\|\\screate\\slocal\\sbackup\\sdirectory\\]", "Data Backup");
        dict.put("PLAY\\s\\[stop\\sCOM\\]", "Start");
        return new LogParser(dict);
    } 
     private ILogParser kvmUpgradeFullbackParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\srestore\\sdata\\]", "Data Restore");
        dict.put("TASK\\:\\s\\[run\\spost\\sreplace\\sscript\\,\\smay\\stake\\saround\\s20\\sminutes\\]", "Post Image Replacemen");
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vms\\s\\|\\sdestroy\\svirtual\\smachine\\]", "Start Upgrade");
        dict.put("TASK\\:\\s\\[vnf\\_prepare\\_vms\\s\\|\\screate\\sdata\\sdirectory\\sfor\\svirtual\\smachine\\]","Prepare Upgrade");
        dict.put("TASK\\:\\s\\[vnf\\_shutdown\\_vms\\s\\|\\sshutdown\\soam\\svirtual\\smachine\\]", "Full Backup");
        dict.put("TASK\\:\\s\\[cal\\_diskspace\\s\\|\\scalculate\\sthe\\shost\\sspace\\]", "GR Check");
        dict.put("TASK\\:\\s\\[failed\\sif\\sGR\\sinstalled\\]", "Health Check");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
    }    
    private ILogParser kvmHealingParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[start\\_com\\s\\|\\sstart\\scom\\sapplication\\]", "Start COM");
        dict.put("TASK\\:\\s\\[vnf\\_start\\_vms\\s\\|\\sstart\\svirtual\\smachine\\]", "Start VM");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
    }    
     
    private ILogParser osUpgradeParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\srestore\\sdata\\]", "Data Restore");
        dict.put("TASK\\:\\s\\[configure\\snew\\sdisk\\sdrive\\]", "Post Configuration");
        dict.put("TASK\\:\\s\\[run\\spost\\sreplace\\sscript\\,\\smay\\stake\\saround\\s20\\sminutes\\]", "Post Image Replacement");
        dict.put("TASK\\:\\s\\[backup\\_data\\s\\|\\sbackup\\sdata\\]", "Data Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser kvmovmInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[Config\\sswitches\\sfor\\sOVM\\]", "Post Configuration");
        dict.put("change\\_kvm\\s\\|\\sCopy\\sqcow2\\sfiles\\sto\\sdirectories","Start VM Instance");
        dict.put("prepare\\s\\|\\sGenerate\\sdata\\ssource\\simage", "Generate Config Driver");
        dict.put("prepare\\s\\|\\sGenerate\\smeta-data", "Start");
        return new LogParser(dict);
    }

    private ILogParser kvmqosacInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("may\\stake\\saround\\s20\\sminutes", "Post Configuration");
        dict.put("change\\_kvm\\s\\|\\sCopy\\sqcow2\\sfiles\\sto\\sdirectories","Start VM Instance");
        dict.put("prepare\\s\\|\\sGenerate\\sdata\\ssource\\simage", "Generate Config Driver");
        dict.put("PLAY\\s\\[Auto\\sinstall\\scom\\son\\skvm\\]", "Start");
        return new LogParser(dict);
    }

    private ILogParser kvmovmUpgradeParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("wait_for_server_start","Start Virtual Machines");
        dict.put("Auto\\sinstall\\scom\\son\\skvm", "Data Backup");
        dict.put("TASK\\:\\s\\[hpsim", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser kvmovmDeleteParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vmfiles\\s\\|\\sdelete\\svirtual\\smachine\\sfiles\\]", "Delete Virtual Machine Files");     
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vms\\s\\|\\sundefine\\svirtual\\smachine\\]","Undefine Virtual Machines");
        dict.put("PLAY\\s\\[destroy\\sall\\svirtual\\smachines\\]", "Destroy Virtual Machines");
        dict.put("\\/usr\\/bin\\/", "Start");
        return new LogParser(dict);
    }
    private ILogParser kvmQosacDeleteParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vmfiles\\s\\|\\sdelete\\svirtual\\smachine\\sfiles\\]", "Delete Virtual Machine Files");     
        dict.put("TASK\\:\\s\\[vnf\\_delete\\_vms\\s\\|\\sundefine\\svirtual\\smachine\\]","Undefine Virtual Machines");
        dict.put("PLAY\\s\\[destroy\\sall\\svirtual\\smachines\\]", "Destroy Virtual Machines");
        dict.put("\\/usr\\/bin\\/", "Start");
        return new LogParser(dict);
    }
    private ILogParser kvmqosacUpgradeParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("PLAY\\s\\[restore\\sdata\\]", "Data Restore");
        dict.put("TASK\\:\\s\\[run\\spost\\sreplace\\sscript", "Post Image Replacement");
        dict.put("TASK\\:\\s\\[ovm\\_prepare\\s\\|\\sGenerate\\suuid\\sfor\\svm\\sinstance\\]","Prepare Virtual Machines");
        dict.put("PLAY\\s\\[backup\\scom\\sdata\\]", "Data Backup");
        dict.put("PLAY\\s\\[stop\\sCOM\\]", "Start");
        return new LogParser(dict);
    }
    private ILogParser kvmqosacBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[backup\\_data\\s\\|\\sbackup\\sdata\\]", "Data Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser kvmqosacRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\screate\\slocal\\srestore\\sdirectory\\]", "Data Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser osqosacBackupParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
    	dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[backup\\_data\\s\\|\\sbackup\\sdata\\]", "Data Backup");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    private ILogParser osqosacRestoreParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\screate\\slocal\\srestore\\sdirectory\\]", "Data Restore");
        dict.put("ansible-playbook", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser kvmarsInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[execute\\sinstall_boe_linux.sh","Install");
        dict.put("TASK\\:\\s\\[create\\sdirectory", "Prepare Environment");
        dict.put("PLAY\\s\\[Install\\sARS", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser osarsInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[execute\\sinstall_boe_linux.sh","Install");
        dict.put("TASK\\:\\s\\[create\\sdirectory", "Prepare Environment");
        dict.put("PLAY\\s\\[Install\\sARS", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser osqosacInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[configure\\snew\\sdisk\\sdrive\\]", "Configure new disk drive");
        dict.put("TASK\\:\\s\\[heat\\_templates\\s\\|\\supdate\\sVNFC\\syaml\\sdocument\\]","Update Document");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Running Credentials");
        dict.put("PLAY\\s\\[create\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser oshpsimInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[post\\_install\\_atc\\s\\|\\sadd\\sswitches\\sto\\sstats\\.cfg\\]", "Config switches for OVM");
        dict.put("TASK\\:\\s\\[heat\\_templates\\s\\|\\supdate\\sVNFC\\syaml\\sdocument\\]","Update Document");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Running Credentials");
        dict.put("PLAY\\s\\[create\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
    }

    private ILogParser osgangliaInstallParser(){
        Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[post\\_install\\_atc\\s\\|\\sadd\\sswitches\\sto\\sstats\\.cfg\\]", "Config switches for OVM");
        dict.put("TASK\\:\\s\\[heat\\_templates\\s\\|\\supdate\\sVNFC\\syaml\\sdocument\\]","Update Document");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Running Credentials");
        dict.put("PLAY\\s\\[create\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser osatcInstallParser(){
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[post\\_install\\_atc\\s\\|\\sadd\\sswitches\\sto\\sstats\\.cfg\\]", "Config switches for OVM");
        dict.put("TASK\\:\\s\\[heat\\_templates\\s\\|\\supdate\\sVNFC\\syaml\\sdocument\\]","Update Document");
        dict.put("TASK\\:\\s\\[os\\_common\\s\\|\\sRunning\\swith\\sOS\\scredentials\\]", "Running Credentials");
        dict.put("PLAY\\s\\[create\\svirtual\\smachines\\]", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser osqosacupgradeParser(){
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("TASK\\:\\s\\[Reboot\\sserver\\]", "Finished");
        dict.put("TASK\\:\\s\\[restore\\_data\\s\\|\\srestore\\sdata\\]", "Data Restore");
        dict.put("TASK\\:\\s\\[configure\\snew\\sdisk\\sdrive\\]", "Configure new disk drive");
        dict.put("TASK\\:\\s\\[rebuild\\_vms\\s\\|\\scheck\\spresence\\sof\\sheat\\sstack\\]", "Heat status");
        dict.put("TASK\\:\\s\\[heat\\_templates\\s\\|\\supdate\\sVNFC\\syaml\\sdocument\\]","Update Document");
        dict.put("PLAY\\s\\[backup\\scom\\sdata\\]", "Data Backup");
        dict.put("PLAY\\s\\[stop\\sCOM\\]", "Start");
        return new LogParser(dict);
    }
    
    private ILogParser osqosacdeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\sdestroy\\sthe\\sstack\\]", "Destroy stack");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\scheck\\spresence\\sof\\sstack\\]", "Check Presence of stack");
        dict.put("GATHERING\\sFACTS", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osatcdeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\sdestroy\\sthe\\sstack\\]", "Destroy stack");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\scheck\\spresence\\sof\\sstack\\]", "Check Presence of stack");
        dict.put("GATHERING\\sFACTS", "Start");
        return new LogParser(dict);
	}
    
    private ILogParser osgangliadeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\sdestroy\\sthe\\sstack\\]", "Destroy stack");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\scheck\\spresence\\sof\\sstack\\]", "Check Presence of stack");
        dict.put("GATHERING\\sFACTS", "Start");
        return new LogParser(dict);
	}

    private ILogParser oshpsimdeleteParser() {
    	Map<String, String> dict = new LinkedHashMap<String, String>();
        dict.put("PLAY\\sRECAP", "Finished");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\sdestroy\\sthe\\sstack\\]", "Destroy stack");
        dict.put("TASK\\:\\s\\[destroy\\_stack\\s\\|\\scheck\\spresence\\sof\\sstack\\]", "Check Presence of stack");
        dict.put("GATHERING\\sFACTS", "Start");
        return new LogParser(dict);
	}
}
