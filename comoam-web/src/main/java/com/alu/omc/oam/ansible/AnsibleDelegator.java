package com.alu.omc.oam.ansible;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.openstack4j.openstack.networking.domain.NeutronSubnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alu.omc.oam.ansible.exception.AnsibleException;
import com.alu.omc.oam.ansible.handler.IAnsibleHandler;
import com.alu.omc.oam.config.Action;
import com.alu.omc.oam.config.ActionKey;
import com.alu.omc.oam.config.AtcOSCOMConfig;
import com.alu.omc.oam.config.CHOSHostnameConfig;
import com.alu.omc.oam.config.CHOSQosacCOMConfig;
import com.alu.omc.oam.config.COMConfig;
import com.alu.omc.oam.config.COMProvidernetworkConfig;
import com.alu.omc.oam.config.HpsimOSCOMConfig;
import com.alu.omc.oam.config.GangliaOSCOMConfig;
import com.alu.omc.oam.config.OSCOMConfig;
import com.alu.omc.oam.config.QosacOSCOMConfig;
import com.alu.omc.oam.log.LogParserFactory;
import com.alu.omc.oam.rest.os.service.NeutronService;
import com.alu.omc.oam.util.NetworkUtil;

@Component
public class AnsibleDelegator implements ApplicationContextAware
{
    
    private static Logger log = LoggerFactory.getLogger(AnsibleDelegator.class);

    private ApplicationContext applicationContext;
    
    IAnsibleInvoker ansibleInvoker; 
    
    @Resource
    private LogParserFactory logParserFactory;
    
    @Resource
    private AnsibleTasks ansibleTasks;


    public void addAnsibleTask(final Action action, final COMConfig config){
        ansibleTasks.create(action, config);
    }
    
    public void execute(String comStack){
        AnsibleTask task = ansibleTasks.get(comStack);
        if(task!=null){
            invoke(task.getAction(), task.getConfig());
            ansibleTasks.remove(comStack);
        }
        
        
    }
    private void invoke(final Action action, final COMConfig config)
    {
        final PlaybookCall playbookCall = new PlaybookCall(config, action);
        try
        {
            ansibleInvoker = (IAnsibleInvoker) applicationContext.getBean("ansibleInvoker");
            new Thread(new Runnable(){

                @Override
                public void run()
                {
                    try
                    {
                        //Thread.currentThread().sleep(1500);
                        ansibleInvoker.invoke(playbookCall, getHandler(action, config));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    
                }
                
            }).start();
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    public void addAnsibleTask(Action action, OSCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           COMProvidernetworkConfig providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    
    public void addAnsibleTask(Action action, QosacOSCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.QosacOSCOMConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 

    public void addAnsibleTask(Action action, CHOSHostnameConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.CHOSHostnameConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    
    public void addAnsibleTask(Action action, CHOSQosacCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.CHOSQosacCOMConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    
    public void addAnsibleTask(Action action, HpsimOSCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.HpsimOSCOMConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    
    public void addAnsibleTask(Action action, GangliaOSCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.GangliaOSCOMConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet!=null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 

    public void addAnsibleTask(Action action, AtcOSCOMConfig config){
        try
        {
           NeutronService neutronService =  (NeutronService)applicationContext.getBean("neutronService");
           com.alu.omc.oam.config.AtcOSCOMConfig.COMProvidernetwork providerNetwork = config.getCom_provider_network();
           NeutronSubnet subnet =  neutronService.getSubetById(providerNetwork.getSubnet());
           if(subnet != null){
               providerNetwork.setDns1(subnet.getDnsNames()!=null && subnet.getDnsNames().size() >0?subnet.getDnsNames().get(0):"8.8.8.8");
               providerNetwork.setGateway(subnet.getGateway());
               providerNetwork.setNetmask(NetworkUtil.getNetMask(subnet.getCidr()));
           }
           if(!StringUtils.isBlank(providerNetwork.getV6_subnet())){
               NeutronSubnet v6subnet =  neutronService.getSubetById(providerNetwork.getV6_subnet());
               if(v6subnet != null){
                   providerNetwork.setV6_gateway(v6subnet.getGateway());
                   providerNetwork.setV6_prefix(NetworkUtil.getNetWorkPrefix(v6subnet.getCidr()));
               }
           }
           ansibleTasks.create(action, config);
        }
        catch (AnsibleException e)
        {
            e.printStackTrace();
        }
    } 
    
    private IAnsibleHandler getHandler(Action action, COMConfig config)
    {
      String handlerBeanName = new ActionKey(action, config.getEnvironment(), config.getCOMType())
      								.toString().concat(IAnsibleHandler.HANDLER_END_FIX);
      IAnsibleHandler handler =   (IAnsibleHandler) applicationContext
                .getBean(handlerBeanName);
      handler.setConfig(config);
      handler.setLogParser(logParserFactory.getLogParser(action, config));
      return handler;
    }
    
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
       this.applicationContext =  applicationContext;
        
    } 
}
