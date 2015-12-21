package com.alu.omc.oam.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class DefaultCommandExecutor  implements ICommandExec 
{
    private String commandLine;
    private Map<String, String> envs = new HashMap<String, String>();
    private File workingDir;
    @Override
    public void  execute( ExecuteResultHandler handler) throws ExecuteException, IOException 
    {
        CommandLine cmdLine = CommandLine.parse(this.commandLine);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workingDir);
        if(handler!=null){
            executor.execute(cmdLine, envs, handler);
        }else{
            executor.execute(cmdLine, envs);
        }

    }
    
    public  DefaultCommandExecutor(String commandLine, File workingDir, Map<String, String> envs){
       this.commandLine = commandLine;
       this.envs = envs;
       this.workingDir = workingDir;
    }

    @Override
    public CommandResult execute() throws IOException, InterruptedException
    {
        CommandLine cmdLine = CommandLine.parse(this.commandLine);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        exec.setStreamHandler(streamHandler);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(2000);
        exec.setWatchdog(watchdog);
        //int res = exec.execute(cmdLine);
        exec.execute(cmdLine, new DefaultExecuteResultHandler());
        try{Thread.sleep(2000);}catch(Exception ee){}
        CommandResult commandResult = new CommandResult(0, outputStream.toString());
        return commandResult;
    }


}
