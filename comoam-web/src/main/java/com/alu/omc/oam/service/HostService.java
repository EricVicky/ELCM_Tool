package com.alu.omc.oam.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alu.omc.oam.ansible.persistence.JsonDataSource;
import com.alu.omc.oam.kvm.model.Host;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class HostService {

	@Resource
	private JsonDataSource dataSource;
	private static Logger log = LoggerFactory.getLogger(WebsocketSender.class);

	public List<String> imagelist(String host, String login, String dir)
			throws Exception {
		List<String> images = new ArrayList<String>();
		// it is for local test only
		if (SystemUtils.IS_OS_WINDOWS) {
			images.add("COM_5_0_0_2.D202.QoSAC.qcow2");
			images.add("COM_5_0_0_2.D192.OAM.qcow2");
			images.add("COM_5_0_0_2.D192.DB.qcow2");
			images.add("QoSAC.qcow2");
			images.add("ATC.qcow2");
			images.add("COM_5_0_0_2.D198.OAM.qcow2");
			images.add("COM_5_0_0_2.D198.DB.qcow2");
			images.add("HPSAM.qcow2");
			images.add("COM_5_0_0_2.D202.OAM.qcow2");
			images.add("COM_5_0_0_2.D202.DB.qcow2");
			images.add("COM_5_0_0_2.D192.QoSAC.qcow2");
			images.add("COM_5_0_0_2.D198.QoSAC.qcow2");
			Collections.sort(images, new IMGComparator());
			return images;
		}else{
			if (Host.isLocalHost(host)) {
				images =  getLocalImages(dir);
			}else{
				String directory = dir;
				String privateKey = "/root/.ssh/id_rsa";
				
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				
				JSch ssh = new JSch();
				ssh.addIdentity(privateKey);
				Session session = ssh.getSession(login, host, 22);
				session.setConfig(config);
				session.connect();
				Channel channel = session.openChannel("sftp");
				channel.connect();
				
				ChannelSftp sftp = (ChannelSftp) channel;
				sftp.cd(directory);
				Vector<ChannelSftp.LsEntry> files = sftp.ls("*");
				System.out.printf("Found %d files in dir %s%n", files.size(), directory);
				
				for (ChannelSftp.LsEntry file : files) {
					if (file.getAttrs().isDir()) {
						continue;
					}
					File obFile = new File(file.getFilename());
					log.info(file.getFilename());
					if(!sizeChanged(obFile) && !file.getFilename().contains("cksum")){
						images.add(file.getFilename());	
					}
				}
				
				channel.disconnect();
				session.disconnect();
			}
			
		}
		Collections.sort(images, new IMGComparator());
		return images;
	}
	
	public boolean sizeChanged(File file) throws InterruptedException{
		long first_size = file.length();
        Thread.sleep(10);
        long second_size = file.length();
        if(first_size == second_size){
        	return false;
        }else{
        	return true;
        }
	}

	public List<String> getLocalImages(String dir) {
		File dirFile = new File(dir);
		log.info("load image list from: " + dirFile.getAbsolutePath());
		File[] files = dirFile.listFiles();
		List<String> images = new ArrayList<String>();
		for (File file : files) {
			try {
				if(!sizeChanged(file) && file.getName().contains("qcow2") && !file.getName().contains("cksum")){
					images.add(file.getName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Collections.sort(images, new IMGComparator());
		}
		return images;
	}

	public List<Host> hostIPs() {
		return dataSource.hosts();

	}

	public static  boolean ping(String host) {
		int  timeOut =  2000;
		
		boolean reachable = false;
		try {
		    InetAddress aa= InetAddress.getByName(host);
			reachable=aa.isReachable(timeOut);
		} catch (Exception e) {
			log.error("Failed to ping ip " + host, e);
		} 
		log.info(host + " is rechable :" + reachable);
		return reachable;
	}
	
	public static void main(String[] args){
	    ping("135.251.236.110");
	}
	
	public class IMGComparator implements Comparator<String> {
//		Pattern IMG_KEY_WORD = Pattern.compile("COM.*D\\d+.*");
		@Override
		public int compare(String img1, String img2) {
/*			if(! IMG_KEY_WORD.matcher(img1).find()){
				return 1;
			}
			if(! IMG_KEY_WORD.matcher(img2).find()){
				return -1;
			}*/
			return img1.compareTo(img2);
		}

	}
}
