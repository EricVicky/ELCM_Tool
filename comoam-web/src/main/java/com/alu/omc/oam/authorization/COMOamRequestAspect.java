package com.alu.omc.oam.authorization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SystemUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alu.omc.oam.ansible.validation.ValidationResult;

@Service
@Aspect
public class COMOamRequestAspect {
	private static Logger log = LoggerFactory.getLogger(COMOamRequestAspect.class);

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	/**
	 * 
	 * The intention here is to match all the RESTful methods under controller package:
	 * <ul>
	 * <li>The package must start with "com.alu.com.webui." and their sub-package of "controller".</li>
	 * <li>The class name must ends with "Controller".</li>
	 * <li>The last parameter of the method must be "WebuiRequestInfo"</li>
	 * </ul>
	 * 
	 */
	@Around("execution(* com.alu.omc.oam..*Controller.*()) && !execution(* com.alu.omc.oam.authorization.AuthController.*())")
	public Object authenticateAround(ProceedingJoinPoint joinPoint) throws Throwable {
		// String task = joinPoint.getSignature().getName();
		// log.info("authenticateAround() is runing with controller: " + task);

		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();
		// log.info("authenticateAround methodName=" + methodName);

		Method[] methods = target.getClass().getDeclaredMethods();

		Method method = null;
		for (Method m : methods) {
			String name = m.getName();
			if (methodName.equals(name)) {
				method = m;
				break;
			}
		}

		// Execute authentication for the request
		String token = request.getHeader("Authorization");
		String user = request.getHeader("username");

		String tokenInSession = (String) request.getSession().getAttribute("token");

		if (token != null && tokenInSession != null && token.equals(tokenInSession)) {
			Object[] args = joinPoint.getArgs();
			Object retValue = joinPoint.proceed(args);
			return retValue;
		} else {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return null;
		}
	}
	@Around("execution(* com.alu.omc.oam.rest.kvm.controller.CheckController.*(..))")
	public Object ignoreCheckAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();
		Method[] methods = target.getClass().getDeclaredMethods();

		Method method = null;
		for (Method m : methods) {
			String name = m.getName();
			if (methodName.equals(name)) {
				method = m;
				break;
			}
		}
		if (SystemUtils.IS_OS_WINDOWS && method.isAnnotationPresent(IgnoreCheck.class)) {
		    ValidationResult rs = new ValidationResult();
		    rs.setSucceed(true);
		    rs.setExist(false);
		    return rs;
		} else {
			Object[] args = joinPoint.getArgs();
			Object retValue = joinPoint.proceed(args);
			return retValue;
		}
	}

	

	private boolean isSecurityTask(String[] tasks) {
		for (String task : tasks) {
			switch (task) {
			case "getServerInfo": // used to get the target COM serve
									// information, e.g.: release, hostname, etc
			case "login": // used to login COM server
			case "getActiveAlarms": // used to get active alarm list from CFMA,
									// CFMA will handle the authentication
				return false;
			default:
			}
		}
		return true;
	}
}
