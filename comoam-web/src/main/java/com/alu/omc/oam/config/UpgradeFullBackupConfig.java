package com.alu.omc.oam.config;

import org.yaml.snakeyaml.Yaml;

import com.alu.omc.oam.ansible.Inventory;
import com.alu.omc.oam.util.YamlFormatterUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UpgradeFullBackupConfig<T extends COMConfig> extends COMConfig
{


	private String full_backup_dir = "";
	private T config;
    
	public String getFull_backup_dir() {
		return full_backup_dir;
	}
	public void setFull_backup_dir(String full_backup_dir) {
		this.full_backup_dir = full_backup_dir;
	}

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
        		+"full_backup_dir: "+YamlFormatterUtil.format(yaml.dump(this.full_backup_dir));
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
        return config.getStackName() ;
    }
}
