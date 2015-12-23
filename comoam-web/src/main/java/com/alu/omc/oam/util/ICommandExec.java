package com.alu.omc.oam.util;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

public interface ICommandExec
{
    public CommandResult execute() throws IOException, InterruptedException; 
    public void execute (ExecuteResultHandler handler)  throws ExecuteException, IOException;
}
