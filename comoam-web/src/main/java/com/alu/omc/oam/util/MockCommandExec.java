/* ------------------------------------------------------------------------------------------
 * Copyright (c) 2005 by Alcatel CIT. All rights reserver
 * ------------------------------------------------------------------------------------------

 * ------------------------------------------------------------------------------------------
 */
package com.alu.omc.oam.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockCommandExec implements ICommandExec {

    private String fileName;
    private File workingDir = null;
    private final String SAMPLE_ANSIBLE_LOG_DIR ="samplelog";

    private static Logger log = LoggerFactory.getLogger(MockCommandExec.class);

    public MockCommandExec(String command, String[] args, String[] envp, File dir) {
        this.fileName = args[0].toLowerCase().concat("_").concat(args[1]).toLowerCase().concat("_").concat(args[2]).concat(".log");
        this.workingDir = dir;
    }

    public CommandResult execute() throws IOException, InterruptedException
    {
        try
        {
            URI uri = MockCommandExec.class
                    .getClassLoader()
                    .getResource(File.separator.concat( SAMPLE_ANSIBLE_LOG_DIR).concat(File.separator).concat(fileName)).toURI();
           File samplelog = new File(uri);
          LineIterator itertor = FileUtils.lineIterator(samplelog);
            File logFile = new File(this.workingDir.getParentFile().getParentFile().getAbsolutePath()
                    .concat(File.separator)
                    .concat("log.txt"));
           FileWriter fw = new FileWriter(logFile);
           Random r = new Random();
          while(itertor.hasNext()){
              String line = itertor.nextLine();
              if(line.length() == 0) continue;
              fw.write(line);
              fw.write(System.lineSeparator());
              fw.flush();
              int sleep =  r.nextInt(300)*1+12;
              Thread.currentThread().sleep(sleep);
          }
          fw.close();
           
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new CommandResult(0, "executed");
    }



    public static void main(String[] args) {
        Pattern p = Pattern.compile("^.*\\\\([\\S]+)\\.yml");
        Matcher m = p.matcher("ansible-playbook -i d:/tmp/ansible/2015-04-07_21-02-55-166\\hosts d:/tmp/ansible/2015-04-07_21-02-55-166\\install_kvm.yml");
       if(m.find()){
          System.out.println( m.group(1));
       } 
    }

    @Override
    public void execute(final ExecuteResultHandler handler) throws ExecuteException,
            IOException
    {
        new Thread( new Runnable(){

            @Override
            public void run()
            {
                try
                {
                    MockCommandExec.this.execute();
                }
                catch (IOException | InterruptedException e)
                {
                    log.error("failed excute the mock command", e);
                    handler.onProcessFailed(new ExecuteException("failed to call ansible" ,1));
                }
                //handler.onProcessComplete(0);
                handler.onProcessFailed(new ExecuteException("faile to excute", 1));
            }
        }).start();
        
    }
}
