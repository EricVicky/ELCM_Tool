package com.alu.omc.oam.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ChannelExec;  
import com.jcraft.jsch.Session;
@Service
public class COMValidationService {
	
	private String username = "root";
	private String ip ;
	private String password = "EMS_qd_n2";
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
            c.put(fsrc, fdest);
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
			try{Thread.sleep(1000);}catch(Exception ee){}
			System.out.println("The command " + command + " is excuted");
			byte[] tmp=new byte[1024];
			while(in.available()>0){
				int i=in.read(tmp, 0, 1024);
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
    	return string;
	}
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Above are defined function. Below are detail function
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String backupPreCheck(String dir){
    	cyFiles2Server("/opt/PlexView/ELCM/script/pre_check_for_fd_backup.sh","/alcatel/omc1/OMC_OSM/backup_scripts/","pre_check_for_fd_backup.sh");
    	String command = "/alcatel/omc1/OMC_OSM/backup_scripts/pre_check_for_fd_backup.sh "+ dir;
    	String checkRes = excuteShell(command);
    	return checkRes;
    }
    
    public String mountServer(String mountDir, String nfsDir, String ip, String nfsIp, String command){
    	String mntCommand = "";
        if("mount".equals(command)){
        	mntCommand = "mount -o nolock -t nfs "+nfsIp+":"+nfsDir+" "+mountDir+" \n";       	
        }else{
        	mntCommand = "umount "+mountDir+" \n";
        }
        String mountRes = excuteShell(mntCommand);
    	return mountRes;
   }
    
    public String ExistCheck(String vm_img_dir,String deployment_prefix,String hostname){
    	String dupCommand = "ls "+vm_img_dir+"/"+deployment_prefix+" | grep "+hostname+"_snapshot";
    	String dupRes = excuteShell(dupCommand);
    	return dupRes;
    }
    
    public void setUserName( String username ){
    	this.username = username;
    }
    
    public void setoamip( String oamip ){
    	this.ip = oamip;
    }
    
    public void setpassword( String password ){
    	this.password = password;
    }
    
    public boolean checkIfCOMProcessUp(String ip){
    	String resutlCheckCOM = excuteShell("CheckCOM");
    	System.out.println("" + resutlCheckCOM);
    	//String resultCheckInstallLog = excuteShell("/install/scripts/checkInstallLog.sh");
    	if(resutlCheckCOM.contains("Number of stopped process(es) : 0") ){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public String preCheckBeforeBackup(String dir){
    	String preCheckResult = backupPreCheck(dir);
    	if(preCheckResult.contains("the backup target name is required in arguments") ){
    		return "Error: the backup target name is required in arguments.";
    	} else if(preCheckResult.contains("NOT existing")){
    		return "Error: The target is NOT existing.";
    	} else if(preCheckResult.contains("NOT writeable")){
    		return "Error: The target is NOT writeable.";
    	} else if(preCheckResult.contains("NOT enough disk space")){
    		return "Error: The target has NOT enough disk space.";
    	} else if(preCheckResult.contains("No such file or directory")){
    		return "Error: Scripts not exist.";
    	} else {
    		return "Success.";
    	}
    }
    
    public String mountNfsServer(String dir, String nfsDir, String ip, String nfsip, String command){
    	String mntResult = mountServer(dir,nfsDir,ip,nfsip,command);
    	return mntResult;
    }
    
    public String backupExistCheck(String deployment_prefix,String vm_img_dir,String hostname){
    	String CheckRes = ExistCheck(vm_img_dir,deployment_prefix,hostname);
    	return CheckRes;
    }
    
    
/*	public static void main(String[] args) {
		// TODO Auto-generated method stub
		COMValidationService sessionToCOM = new COMValidationService();
		sessionToCOM.setoamip("10.223.1.85");
		sessionToCOM.setUserName("root");
		sessionToCOM.setpassword("newsys");
        boolean syl = sessionToCOM.checkCOM();
		System.out.println("" + syl);

	}*/

}
