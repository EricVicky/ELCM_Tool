package com.alu.omc.oam.authorization;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alu.omc.oam.ansible.persistence.JsonDataSource;
import com.alu.omc.oam.config.COMStack;
import com.alu.omc.oam.config.GRInstallConfig;
import com.alu.omc.oam.config.GRROLE;
import com.alu.omc.oam.config.KVMCOMConfig;
import com.alu.omc.oam.config.Status;
import com.alu.omc.oam.config.VMConfig;
import com.alu.omc.oam.service.COMStackService;
import com.alu.omc.oam.service.COMValidationService;
import com.alu.omc.oam.util.Json2Object;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Service
@Aspect

public class GRStatusAspect implements  Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4597002583194942399L;
	@Resource
	@Autowired
    COMValidationService cOMValidationService;
	@Resource
	@Autowired
    COMStackService cOMStackService;
	@Resource
    private JsonDataSource dataSource;
    
	private static Logger log = LoggerFactory.getLogger(GRStatusAspect.class);

	@Around("execution(* com.alu.omc.oam.service..*COMStackService.*())")
	public Object authenticateAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();//method=list
		Method[] methods = target.getClass().getDeclaredMethods();
		Method method = null;
		for (Method m : methods) {
			String name = m.getName();
			if (methodName.equals(name)) {
				method = m;//method=list
				break;
			}
		}
    	List<COMStack> stacks =  dataSource.list();
		for(COMStack stack : stacks){
			KVMCOMConfig config = getKVMCOMConfig(stack.getName());
			Map<String, VMConfig> vmconfigs = config.getVm_config();
			Iterator<String> iterator = vmconfigs.keySet().iterator();
	    	while(iterator.hasNext()){
	    		String vnfc = iterator.next();
	    		if(("oam").equals(vnfc)){
	    			VMConfig vmConfig = vmconfigs.get(vnfc);
	    			String oamIP = vmConfig.getNic().get(0).getIp_v4().getIpaddress();
	    			cOMValidationService.setIp(oamIP);
	    			break;
	    		}
	    	}
	    	String checkRes = cOMValidationService.updateGRRole();
			if(checkRes.contains("act")){
				stack.setRole(GRROLE.Primary);
				dataSource.save(stacks);
			}else if (checkRes.contains("stby")){
				stack.setRole(GRROLE.Secondary);
				dataSource.save(stacks);
			}else{
				continue;
			}
		}
		Object[] args = joinPoint.getArgs();
		Object retValue = joinPoint.proceed(args);
		return retValue;
	}
	
	private KVMCOMConfig getKVMCOMConfig(String stackName){
        COMStack comStack = cOMStackService.get(stackName); 
        return new Json2Object<KVMCOMConfig>(){}.toMap(comStack.getComConfig());
    }

}
