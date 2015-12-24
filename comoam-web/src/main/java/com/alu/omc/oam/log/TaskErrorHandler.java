package com.alu.omc.oam.log;

import java.io.Serializable;

import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.Environment;

public class TaskErrorHandler<T extends COMConfig>  implements Serializable
{
/**
      * @Fields serialVersionUID : 
      */
    private static final long serialVersionUID = 545184223846187256L;
Action handleAction;
Environment env;
T config;
public TaskErrorHandler(Action handleAction, Environment env, T config)
{
    super();
    this.handleAction = handleAction;
    this.env = env;
    this.config = config;
}

public Action getHandleAction()
{
    return handleAction;
}

public void setHandleAction(Action handleAction)
{
    this.handleAction = handleAction;
}

public Environment getEnv()
{
    return env;
}

public void setEnv(Environment env)
{
    this.env = env;
}

public T getConfig()
{
    return config;
}

public void setConfig(T config)
{
    this.config = config;
}

public TaskErrorHandler(){
    
}

}
