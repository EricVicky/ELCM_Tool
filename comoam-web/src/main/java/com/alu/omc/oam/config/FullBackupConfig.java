package com.alu.omc.oam.config;


import org.yaml.snakeyaml.Yaml;

import com.alu.omc.oam.ansible.Inventory;
import com.alu.omc.oam.util.YamlFormatterUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FullBackupConfig<T extends COMConfig> extends COMConfig
{


	public String stackName;
	private String full_backup_dir = "";

    private String remote_server_dir  = "";  
	private String remote_server_ip = "";
	private boolean fast_mode = false;
	private T config;
    
	public boolean getFast_mode()
	{
	    return fast_mode;
	}
	public void setFast_mode(boolean fast_mode)
	{
	    this.fast_mode = fast_mode;
	}
	public String getFull_backup_dir() {
		return full_backup_dir;
	}
	public void setFull_backup_dir(String full_backup_dir) {
		this.full_backup_dir = full_backup_dir;
	}
	public String getRemote_server_dir() {
		return remote_server_dir;
	}
	public void setRemote_server_dir(String remote_server_dir) {
		this.remote_server_dir = remote_server_dir;
	}
	public String getRemote_server_ip() {
		return remote_server_ip;
	}
	public void setRemote_server_ip(String remote_server_ip) {
		this.remote_server_ip = remote_server_ip;
	}

    @JsonIgnore
	public T getConfig() {
		return config;
	}
 
	public void setConfig(T config) {
		this.config = config;
	}

	@Override
    @JsonIgnore
    public Inventory getInventory()
    {
        return config.getInventory();
    }

    @Override
    @JsonIgnore
    public String getVars()
    {
    	Yaml yaml = new Yaml();
        return config.getVars()
        		+"full_backup_dir: "+YamlFormatterUtil.format(yaml.dump(this.full_backup_dir))
        		+"remote_server_dir: "+YamlFormatterUtil.format(yaml.dump(this.remote_server_dir))
        		+"remote_server_ip: "+YamlFormatterUtil.format(yaml.dump(this.remote_server_ip))
        		+"fast_mode: "+YamlFormatterUtil.format(yaml.dump(this.fast_mode));
    }

    @Override
    @JsonIgnore
    public Environment getEnvironment()
    {
       return config.getEnvironment();
    }


    @Override
    public COMType getCOMType()
    {
        return  config.getCOMType() ;
    }

    @Override
    public String getStackName()
    {
        return this.stackName;
    }

    public void setStackName(String stackName)
    {
        this.stackName = stackName;
    }

}
