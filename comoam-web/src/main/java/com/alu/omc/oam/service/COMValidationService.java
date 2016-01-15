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
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
@Service
public class COMValidationService {
	private final String SOURCE = "/opt/PlexView/ELCM/script/";
	private final String DESTINATION =	"/tmp/";
	private final String START_POINT =	"precheck start";
 
	@Resource
    private  CommandProtype commandProtype;
	private String username = "root";
	private String ip ;
	private String password = "newsys";
	
    public void setUserName( String username ){
    	this.username = username;
    }
    
    public void setIp( String ip ){
    	this.ip = ip;
    }
    
    public void setpassword( String password ){
    	this.password = password;
    }
	//windows OS
	private Session getSession(String username, String ip, String password){
        JSch shell = new JSch();
        Session session = null;
        try {
			session = shell.getSession(username, ip, 22);
	        session.setPassword(password);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.connect();
	        System.out.println("The session to COM server " + ip + " is created");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return session;
	}
	//Linux OS
	private Session getSession(String ip){
        String privateKey = "/root/.ssh/id_rsa";
        
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        
        JSch ssh = new JSch();
        try
        {
            ssh.addIdentity(privateKey);
            Session session = ssh.getSession("root", ip, 22);
            System.out.println("The session to COM server " + ip + " is created");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            return session;
        }
        catch (JSchException e)
        {
            e.printStackTrace();
        }
        return null;
	}
	//Get channel
	private Channel getChannel(Session session,String protocol){
		Channel channel = null;
		try {
			channel = session.openChannel(protocol);
			System.out.println("The "+protocol+" channel is created");
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
    	return channel;
	}
	
	public void cyFiles2Server(String src, String dest, String file){
		Session session = null;
        if(SystemUtils.IS_OS_WINDOWS){
            session = getSession(this.username, this.ip, this.password);
        }else{
            session = getSession(this.ip);
        } 
		Channel channel = getChannel(session,"sftp");
        ChannelSftp c = null;
        try {
        	c = (ChannelSftp) channel;
            System.out.println("Starting File Upload:");
            String fsrc = src, fdest = dest;
            c.put(fsrc+file, fdest);
            c.chmod(744, fdest+file);
            //c.get(fdest, "/tmp/testfile.bin");
            c.disconnect();
        } catch (Exception e) {	
        	e.printStackTrace();	
        } finally {
        	channel.disconnect();
            session.disconnect();
        }
	}
	
    public void opFirewall(String command){
    	Session session = null;
        if(SystemUtils.IS_OS_WINDOWS){
            session = getSession(this.username, this.ip, this.password);
        }else{
            session = getSession(this.ip);
        } 
        Channel channel = getChannel(session,"shell");
		String finalCommand = command+"\n";
		System.out.println("The firewall command: " + command);
		try {
    		OutputStream outstream = channel.getOutputStream();
			outstream.write(finalCommand.getBytes());
			outstream.flush();
			try{Thread.sleep(1000);}catch(Exception ee){}
			System.out.println("The firewall command " + command + " is excuted");
            outstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            channel.disconnect();
            session.disconnect();
		} 
    }
    
	public String excuteShell(String command){
		Session session = null;
        if(SystemUtils.IS_OS_WINDOWS){
            session = getSession(this.username, this.ip, this.password);
        }else{
            session = getSession(this.ip);
        } 
        Channel channel = getChannel(session,"shell");
		String finalCommand = command+"\n";
		String string = null;
    	try {
    		OutputStream outstream = channel.getOutputStream();
    		InputStream in=channel.getInputStream();
			outstream.write(finalCommand.getBytes());
			outstream.flush();
			try{Thread.sleep(5000);}catch(Exception ee){}
			System.out.println("The command " + command + " is excuted");
			byte[] tmp=new byte[2048];
			while(in.available()>0){
				int i=in.read(tmp, 0, 2048);
				if(i<0)break;
				string = new String(tmp, 0, i);
				System.out.print(string);
		    }
			try{Thread.sleep(1000);}catch(Exception ee){}
            outstream.close();
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            channel.disconnect();
            session.disconnect();
		}    	
    	return trim(string);
	}
	
	private String trim(String stdout){
		int fix_promote_lines=0;	
		System.out.println("stdout:"+stdout);
		StringBuffer res = new StringBuffer();
		if(stdout!=null && stdout.length() > 0){
			String[] lines = stdout.split("\r\n");
			System.out.println("The lines: " + lines);
			for(int i=0;i<lines.length;i++){
				if(lines[i].equals(START_POINT)){
					fix_promote_lines = i;
					System.out.println("fix_promote_lines:"+fix_promote_lines);
				}
			}
			if(lines.length >= fix_promote_lines){
				for(int i=fix_promote_lines+1; i< lines.length; i++){
					if(lines[i].endsWith("# "))
						break;
					res.append(lines[i]);
					if(i<lines.length-2){
						res.append("\n");	
					}
				}
			}else{
				System.out.print("abnormal output");
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
    	System.out.println("" + resutlCheckCOM);
    	//String resultCheckInstallLog = excuteShell("/install/scripts/checkInstallLog.sh");
    	if(resutlCheckCOM.contains("Number of stopped process(es) : 0") ){
    		return true;
    	} else {
    		return false;
    	}
    } 
    
    public String cpuVTCheck(String hostip){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
    	if(Host.isLocalHost(hostip)){
    		String script = source+"check_VT_enable.sh";
    		ICommandExec comamnda = commandProtype.create(script);
    		try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = res.getOutputString();
            }catch(Exception e){
            	e.printStackTrace();
            }
    	}else{
    		cyFiles2Server(source,destination,"check_VT_enable.sh");
    		String script = destination+"check_VT_enable.sh";
    		checkRes = excuteShell(script);		
    	}
    	return checkRes;
    } 
    
    private String deal(String stdout){
    	String[] lines = stdout.split("\n");
    	return lines[1]+lines[2];
    }
    
    public String fullbackupPreCheck(String hostip,String local_backup_dir,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
		String remote_backup_dir = remoteip == ""?"":remoteip + ":" + remotedir;
    	if(Host.isLocalHost(hostip)){
    		String script = source+"fullbackup_precheck.sh";
    		ICommandExec comamnda = commandProtype.create(script+" "+local_backup_dir+" "+hostname+" "+remote_backup_dir);
    	    try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = deal(res.getOutputString());
            }catch(Exception e){
            	e.printStackTrace();
            }
    	}else{
    		cyFiles2Server(source,destination,"fullbackup_precheck.sh");
    		String script = destination+"fullbackup_precheck.sh";
    		checkRes = excuteShell(script+" "+local_backup_dir+" "+hostname+" "+remote_backup_dir);	
    	}
    	return checkRes;
    }
    
    public String fullrestorePreCheck(String hostip,String local_backup_dir,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION; 
    	String remote_backup_dir = remoteip == ""?"":remoteip + ":" + remotedir;
		if(Host.isLocalHost(hostip)){
    		String script = source+"fullrestore_precheck.sh";
    		ICommandExec comamnda = commandProtype.create(script+" "+local_backup_dir+" "+hostname+" "+remote_backup_dir);
    	    try{
    	        CommandResult res = comamnda.execute();
    	        checkRes = res.getOutputString(); 
            }catch(Exception e){
            	e.printStackTrace();
            }
    	}else{
    		cyFiles2Server(source,destination,"fullrestore_precheck.sh");
    		String script = destination+"fullrestore_precheck.sh";
    		checkRes = excuteShell(script+" "+local_backup_dir+" "+hostname+" "+remote_backup_dir);	
    	}
    	return checkRes;
    }
    
    public String databackupPreCheck(String localdir,String filename,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION;   
    	cyFiles2Server(source,destination,"databackup_precheck.sh");
    	String script = destination+"databackup_precheck.sh";
    	String local_backup_dir = localdir;
    	String remote_backup_dir = remoteip == ""?"":remoteip + ":" + remotedir;
    	try {
    		opFirewall("service iptables stop");
    		checkRes = excuteShell(script+" "+local_backup_dir+" "+filename+" "+remote_backup_dir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			opFirewall("service iptables start");
		}
    	return checkRes;
    }
    
    public String datarestorePreCheck(String localdir,String filename,String hostname,String remoteip,String remotedir){
    	String checkRes = "";
    	String source = SOURCE;
    	String destination = DESTINATION;   	
    	cyFiles2Server(source,destination,"datarestore_precheck.sh");
    	String script = destination+"datarestore_precheck.sh";
    	String local_backup_dir = localdir;
    	String remote_backup_dir = remoteip == ""?"":remoteip + ":" + remotedir;
    	try {
    		opFirewall("service iptables stop");
    		checkRes = excuteShell(script+" "+local_backup_dir+" "+hostname+" "+filename+" "+remote_backup_dir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			opFirewall("service iptables start");
		}
    	return checkRes;
    }

}
