package com.alu.omc.oam.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.exec.environment.EnvironmentUtils;
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
	
	private final class COMMAND{
		private static final String ROOT = "root";
		private static final String SOURCE = "/opt/PlexView/ELCM/script/";
		private static final String DESTINATION =	"/tmp/";
		private static final String STRICT_HOST_KEY_CK = "StrictHostKeyChecking";
		private static final String START_FIREWALL = "service iptables start";
		private static final String STOP_FIREWALL = "service iptables stop";
		private static final String CHECK_VT_SH = "check_VT_enable.sh";
		private static final String FULLBACKUP_SH = "fullbackup_precheck.sh";
		private static final String FULLRESTORE_SH = "fullrestore_precheck.sh";
		private static final String DATABACKUP_SH = "databackup_precheck.sh";
		private static final String DATARESTORE_SH = "datarestore_precheck.sh";
		private static final String REPLICATE_DATA = "grReplicateData.sh";
		private static final String GETGRROLE = "get_GRRole.sh";
		private static final String FDADDIPV6 = "/install/scripts/decomposed/common/fd_add_ipv6.sh";
	}
 
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
    	        session.setConfig(COMMAND.STRICT_HOST_KEY_CK, "no");
    	        session.connect();
    		}catch (JSchException e) { // NOSONAR
    			e.printStackTrace(); // NOSONAR
    		} 
    	}else{
    		String privateKey = "/root/.ssh/id_rsa";
            java.util.Properties config = new java.util.Properties();
            config.put(COMMAND.STRICT_HOST_KEY_CK, "no");
            try
            {
            	jsch.addIdentity(privateKey);
                session = jsch.getSession(COMMAND.ROOT, this.ip, this.port);
                session.setConfig(COMMAND.STRICT_HOST_KEY_CK, "no");
                session.setTimeout(1000);
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
    		session.setTimeout(800);
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
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {	// NOSONAR
        	e.printStackTrace();	// NOSONAR
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
    	return string;
	}
	
	public String ipv6Config(String command){
		String res = "";
		Session session = getSession();
		try {
			Channel channel = session.openChannel("exec");
			res = exeCommand(command,session,channel);
		} catch (Exception e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		}	
		return res;
	}
	
	public String getGRRole(String script,String command){
		String res = "";
		cyFiles2Server(COMMAND.SOURCE,COMMAND.DESTINATION,script);
		Session session = getSession();
		try {
			Channel channel = session.openChannel("exec");
			res = exeCommand(command,session,channel);
		} catch (Exception e) {// NOSONAR
			e.printStackTrace();// NOSONAR
		}	
		return res;	
	}
	
    public String preCheck(String script,String command){
    	String res = "";
    	if(Host.isLocalHost(this.ip)){
    		System.out.println(COMMAND.SOURCE+command.substring(5));
    		ICommandExec comamnda = commandProtype.create(COMMAND.SOURCE+command.substring(5));
    	    try{
    	        CommandResult commandRes = comamnda.execute();
    	        res = commandRes.getOutputString();
            }catch(Exception e){// NOSONAR
            	e.printStackTrace();// NOSONAR
            }
    	}else{
    		cyFiles2Server(COMMAND.SOURCE,COMMAND.DESTINATION,script);
    		Session session = getSession();
    		try {
    			opFirewall(COMMAND.STOP_FIREWALL);
    			Channel channel = session.openChannel("exec");
    			res = exeCommand(command,session,channel);
    		} catch (Exception e) {// NOSONAR
    			e.printStackTrace();// NOSONAR
    		} finally {
    			opFirewall(COMMAND.START_FIREWALL);
    		}	
    	}
		return res;	
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
    
    public String cpuVirtualCheck(){
    	String command = COMMAND.DESTINATION+COMMAND.CHECK_VT_SH;
    	return preCheck(COMMAND.CHECK_VT_SH,command);
    }
    
    public String preCheckOfFullRestore(String localdir,String hostname,String remoteip,String remotedir){
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	String command = COMMAND.DESTINATION+COMMAND.FULLRESTORE_SH+" "+localdir+" "+hostname+" "+remoteBackupDir;
    	return preCheck(COMMAND.FULLRESTORE_SH,command);
    }
    
    public String preCheckOfFullBackup(String localdir,String hostname,String remoteip,String remotedir){
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	String command = COMMAND.DESTINATION+COMMAND.FULLBACKUP_SH+" "+localdir+" "+hostname+" "+remoteBackupDir;
    	return preCheck(COMMAND.FULLBACKUP_SH,command);
    }
    
    public String preCheckOfDataBackup(String localdir,String filename,String remoteip,String remotedir){
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	String command = COMMAND.DESTINATION+COMMAND.DATABACKUP_SH+" "+localdir+" "+filename+" "+remoteBackupDir;
		return preCheck(COMMAND.DATABACKUP_SH,command);
    }
    
    public String preCheckOfDataRestore(String localdir,String filename,String hostname,String remoteip,String remotedir){
    	String remoteBackupDir = remoteip == ""?"":remoteip + ":" + remotedir;
    	String command = COMMAND.DESTINATION+COMMAND.DATARESTORE_SH+" "+localdir+" "+hostname+" "+filename+" "+remoteBackupDir;
    	return preCheck(COMMAND.DATARESTORE_SH,command);
    }
    
    public String grReplicateData(String script){
    	String command = COMMAND.DESTINATION+script;
    	return preCheck(COMMAND.REPLICATE_DATA,command);
    }
    
    public String updateGRRole(){
    	String command = COMMAND.DESTINATION+COMMAND.GETGRROLE;
    	return getGRRole(COMMAND.GETGRROLE,command);
    }
    
    public String addIpv6(){
    	String command = COMMAND.FDADDIPV6;
    	return ipv6Config(command);
    }

}
