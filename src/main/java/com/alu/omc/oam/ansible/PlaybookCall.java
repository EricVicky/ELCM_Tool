package com.alu.omc.oam.ansible;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alu.omc.oam.ansible.exception.WorkspaceException;
import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.COMConfig;

public class PlaybookCall
public class PlaybookCall implements AnsibleCall
{
/**
  * @Fields playbook : the file name of playbook 
  */
private Playbook playbook;
private String parameter;
private Inventory inventory;
private String vars;
private String cfg;
private final String VAR_FILE_NAME = "group_vars/all";
private final String HOSTS_FILE_NAME = "hosts";
private static final Log log = LogFactory.getLog(PlaybookCall.class);
    /**
     * @Fields playbook : the file name of playbook
     */
    private Playbook            playbook;

public PlaybookCall(COMConfig config, Action action){
   this.inventory = config.getInventory();
   this.vars = config.getVars();
   this.cfg = config.getCfg();
   this.playbook = PlaybookFactory.getInstance().getPlaybook(action, config);
}
public String prepare(Ansibleworkspace space){
    try
    private static Logger       log             = LoggerFactory
                                                        .getLogger(PlaybookCall.class);
    private COMConfig           config;
    private static final String ANSIBLE_COMMAND = "ansible-playbook ";

    public PlaybookCall(COMConfig config, Action action)
    {
    	log.info("Write var file to working directory...");
        FileUtils.writeStringToFile(new File(space.getWorkingdir().concat(VAR_FILE_NAME)), this.vars);
        log.info("Write host file to working directory...");
        FileUtils.writeStringToFile(new File(space.getWorkingdir().concat(HOSTS_FILE_NAME)), this.inventory.toInf()); 
        log.info("Copy ansible codes to working directory...");
        FileUtils.copyDirectory(new File(space.getWorkDirRoot() + "code"), new File(space.getWorkingdir()));
        FileUtils.writeStringToFile(new File(space.getWorkingdir() + "ansible.cfg"), 
        		cfg.concat("\r\n").concat("log_path=" + space.getLogFile()));
        log.info("Write empty log file");
        FileUtils.write(space.getLogFile(), "-------Call ansible......");
        this.playbook = PlaybookFactory.getInstance().getPlaybook(action,
                config);
        this.config  = config;








    }
    catch (IOException e)

    public String prepare(Ansibleworkspace space)
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
        try
        {
            space.init(config);
        }
        catch (WorkspaceException e)
        {
            e.printStackTrace();
            return null;
        }
        return ANSIBLE_COMMAND.concat("-i ").concat(
                space.getWorkingdir() + Ansibleworkspace.HOSTS_FILE_NAME + " "
                        + this.playbook.getFilePath(space));
    }
    return "-i " + space.getWorkingdir() + HOSTS_FILE_NAME + " " + this.playbook.getFilePath(space);
}



}
