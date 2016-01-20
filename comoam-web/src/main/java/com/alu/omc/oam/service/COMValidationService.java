package com.alu.omc.oam.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import com.alu.omc.oam.kvm.model.Host;
import com.alu.omc.oam.util.CommandProtype;
import com.alu.omc.oam.util.CommandResult;
import com.alu.omc.oam.util.ICommandExec;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
@Service
public class COMValidationService {
	private static final String ROOT = "root";
	private static final String SOURCE = "/opt/PlexView/ELCM/script/";
	private static final String DESTINATION =	"/tmp/";
	private static final String START_POINT =	"precheck start";
	private static final String STRICT_HOST_KEY_CK = "StrictHostKeyChecking";
	private static final String CHECK_VT_SH = "check_VT_enable.sh";
	private static final String FULLBACKUP_SH = "fullbackup_precheck.sh";
	private static final String FULLRESTORE_SH = "fullrestore_precheck.sh";
	private static final String DATABACKUP_SH = "databackup_precheck.sh";
	private static final String DATARESTORE_SH = "datarestore_precheck.sh";
 
	@Resource
    private  CommandProtype commandProtype;
	private String username = "root";
	private String ip ;
	private int port = 22;
	private String password = "newsys"; // NOSONAR
	
    public void setUserName( String username ){
    	this.username = username;
    }
    
    public void setIp( String ip ){
    	this.ip = ip;
    }
    
    public void setpassword( String password ){
    	this.password = password;
    }
    
    private Session getSession(){
    	JSch jsch = new JSch();
    	Session session = null; 	
    	if(SystemUtils.IS_OS_WINDOWS){
    		try{
    			session = jsch.getSession(this.username, this.ip, this.port);
    	        session.setPassword(password);
    	        session.setConfig(STRICT_HOST_KEY_CK, "no");
    	        session.connect();
    		}catch (JSchException e) { // NOSONAR
    			e.printStackTrace(); // NOSONAR
    		} 
    	}else{
    		String privateKey = "/root/.ssh/id_rsa";
            java.util.Properties config = new java.util.Properties();
            config.put(STRICT_HOST_KEY_CK, "no");
            try
            {
            	jsch.addIdentity(privateKey);
                session = jsch.getSession(ROOT, this.ip, this.port);
                session.setConfig(STRICT_HOST_KEY_CK, "no");
                session.connect();
            }
            catch (JSchException e){ // NOSONAR
                e.printStackTrace();// NOSONAR
            }
    	}
    	return session;
    }
    
    public String exeCommand(String command,Session session,Channel channel){
    	String result = "";
    	StringBuilder exeRes = new StringBuilder();
    	try{
    		session.setTimeout(5000);
    		((ChannelExec) channel).setCommand(command);
    		channel.setInputStream(null);
    		((ChannelExec) channel).setErrStream(System.err);
	        InputStream in = channel.getInputStream();
	        channel.connect();
	        byte[] tmp = new byte[1024];
	        while (true) {
	            while (in.available() > 0) {
	                int i = in.read(tmp, 0, 1024);
	                if (i < 0){
	                	break;	
	                }
	                result = new String(tmp, 0, i);
	                exeRes.append(result);
	            }
	            if (channel.isClosed()) {
	                break;
	            }
	            try {
	                Thread.sleep(1000);
	            } catch (Exception e) {
	            	result = e.toString();
	            }
	        }
	        channel.disconnect();
	        session.disconnect();
    	}catch (Exception e) {
    		result = e.toString();
	    }
    	System.out.println("result is:"+exeRes.toString());
    	return exeRes.toString();
    }

	//Get channel
	private Channel getChannel(Session session,String protocol){
		Channel channel = null;
		try {
			channel = session.openChannel(protocol);
			System.out.println("The "+protocol+" channel is created"); // NOSONAR
			channel.connect();
		} catch (JSchException e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		}
    	return channel;
	}
	
	public void cyFiles2Server(String src, String dest, String file){
		Session session = getSession();
		Channel channel = getChannel(session,"sftp");
        ChannelSftp c = null;
        try {
        	c = (ChannelSftp) channel;
            System.out.println("Starting File Upload:"); // NOSONAR
            String fsrc = src;
            String fdest = dest;
            c.put(fsrc+file, fdest);
            c.chmod(744, fdest+file);
            c.disconnect();
        } catch (Exception e) {	// NOSONAR
        	e.printStackTrace();	// NOSONAR
        } finally {
        	channel.disconnect();
            session.disconnect();
        }
	}
	
	public void sleep(int time){
		try{
			Thread.sleep(time);
		}catch(Exception e){ // NOSONAR
			e.printStackTrace(); // NOSONAR
		}
	}
	
    public void opFirewall(String command){
    	Session session = getSession();
        Channel channel = getChannel(session,"shell");
		String finalCommand = command+"\n";
		System.out.println("The firewall command: " + command); // NOSONAR
		try {
    		OutputStream outstream = channel.getOutputStream();
			outstream.write(finalCommand.getBytes());
			outstream.flush();
			sleep(1000);
			System.out.println("The firewall command " + command + " is excuted"); // NOSONAR
            outstream.close();
		} catch (IOException e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		} finally {
            channel.disconnect();
            session.disconnect();
		} 
    }
    
	public String excuteShell(String command){
		Session session = getSession();
        Channel channel = getChannel(session,"shell");
		String finalCommand = command+"\n";
		String string = null;
    	try {
    		OutputStream outstream = channel.getOutputStream();
    		InputStream in=channel.getInputStream();
			outstream.write(finalCommand.getBytes());
			outstream.flush();
			sleep(5000);
			System.out.println("The command " + command + " is excuted"); // NOSONAR
			byte[] tmp=new byte[2048];
			while(in.available()>0){
				int i=in.read(tmp, 0, 2048);
				if(i<0){
					break;	
				}
				string = new String(tmp, 0, i);
				System.out.print(string); // NOSONAR
		    }
			sleep(1000);
            outstream.close();
            in.close();
		} catch (IOException e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		} finally {
            channel.disconnect();
            session.disconnect();
		}    	
    	return trim(string);
	}
	
	private String trim(String stdout){
		int fixPromoteLines=0;	
		System.out.println("stdout:"+stdout); // NOSONAR
		StringBuilder res = new StringBuilder();
		if(stdout!=null && stdout.length() > 0){
			String[] lines = stdout.split("\r\n");
			System.out.println("The lines: " + lines); // NOSONAR
			for(int i=0;i<lines.length;i++){
				if(lines[i].equals(START_POINT)){
					fixPromoteLines = i;
					System.out.println("fix_promote_lines:"+fixPromoteLines); // NOSONAR
				}
			}
			for(int i=fixPromoteLines+1; i< lines.length; i++){
				if(lines[i].endsWith("# "))
					break;
				res.append(lines[i]);
				if(i<lines.length-2){
					res.append("\n");	
				}
			}
		}
		return res.toString();
	}
	

	
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Above are defined function. Below are detail function
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    
    public boolean checkIfCOMProcessUp(String ip){
    	this.ip = ip;
    	String resutlCheckCOM = excuteShell("CheckCOM");
    	System.out.println("" + resutlCheckCOM); // NOSONAR
    	return resutlCheckCOM.contains("Number of stopped process(es) : 0")?true:false;
    } 
    
    public String cpuVTCheck(String hostip){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
    	if(Host.isLocalHost(hostip)){
    		String script = source+CHECK_VT_SH;
    		ICommandExec comamnda = commandProtype.create(script);
    		try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = res.getOutputString();
            }catch(Exception e){ // NOSONAR
            	e.printStackTrace();// NOSONAR
            }
    	}else{
    		cyFiles2Server(source,destination,CHECK_VT_SH);
    		String script = destination+CHECK_VT_SH;
    		checkRes = excuteShell(script);		
    	}
    	return checkRes;
    } 
    
    private String deal(String stdout){
    	String[] lines = stdout.split("\n");
    	return lines[1]+lines[2];
    }
    
    public String fullbackupPreCheck(String hostip,String localBackupDir,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
		String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	if(Host.isLocalHost(hostip)){
    		String script = source+FULLBACKUP_SH;
    		ICommandExec comamnda = commandProtype.create(script+" "+localBackupDir+" "+hostname+" "+remoteBackupDir);
    	    try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = deal(res.getOutputString());
            }catch(Exception e){// NOSONAR
            	e.printStackTrace();// NOSONAR
            }
    	}else{
    		cyFiles2Server(source,destination,FULLBACKUP_SH);
    		String script = destination+FULLBACKUP_SH;
    		checkRes = excuteShell(script+" "+localBackupDir+" "+hostname+" "+remoteBackupDir);	
    	}
    	return checkRes;
    }
    
    public String fullrestorePreCheck(String hostip,String localBackupDir,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
		if(Host.isLocalHost(hostip)){
    		String script = source+FULLRESTORE_SH;
    		ICommandExec comamnda = commandProtype.create(script+" "+localBackupDir+" "+hostname+" "+remoteBackupDir);
    	    try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = deal(res.getOutputString()); 
            }catch(Exception e){// NOSONAR
            	e.printStackTrace();// NOSONAR
            }
    	}else{
    		cyFiles2Server(source,destination,FULLRESTORE_SH);
    		String script = destination+FULLRESTORE_SH;
    		checkRes = excuteShell(script+" "+localBackupDir+" "+hostname+" "+remoteBackupDir);	
    	}
    	return checkRes;
    }
    
    public String databackupPreCheck(String localdir,String filename,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION;   
    	cyFiles2Server(source,destination,DATABACKUP_SH);
    	String script = destination+DATABACKUP_SH;
    	String localBackupDir = localdir;
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	try {
    		opFirewall("service iptables stop");
    		checkRes = excuteShell(script+" "+localBackupDir+" "+filename+" "+remoteBackupDir);
		} catch (Exception e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		} finally {
			opFirewall("service iptables start");
		}
    	return checkRes;
    }
    
    public String datarestorePreCheck(String localdir,String filename,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION;   	
    	cyFiles2Server(source,destination,DATARESTORE_SH);
    	String script = destination+DATARESTORE_SH;
    	String localBackupDir = localdir;
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	try {
    		opFirewall("service iptables stop");
    		checkRes = excuteShell(script+" "+localBackupDir+" "+hostname+" "+filename+" "+remoteBackupDir);
		} catch (Exception e) { // NOSONAR
			e.printStackTrace(); // NOSONAR
		} finally {
			opFirewall("service iptables start");
		}
    	return checkRes;
    }
    
    public String grReplicateData(String command){
    	Session session = getSession();
    	Channel channel = null;
    	System.out.println("command is :"+command);
		try {
			channel = session.openChannel("shell");
		} catch (JSchException e1) { // NOSONAR
			e1.printStackTrace(); // NOSONAR
		}
		return exeCommand(command,session,channel);
    }

}
