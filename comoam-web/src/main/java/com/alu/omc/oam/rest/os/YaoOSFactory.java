package com.alu.omc.oam.rest.os;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.types.Facing;
import org.openstack4j.openstack.identity.domain.KeystoneAccess;
import org.openstack4j.openstack.internal.OSClientSession;

import com.alu.omc.oam.rest.os.service.access.YaoAccess;


public abstract class YaoOSFactory {
    
    /**
     * Skips Authentication and created the API around a previously cached Access object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours
     * 
     * @param access an authorized access entity which is to be used to create the API
     * @param config OpenStack4j configuration options
     * @return the OSCLient
     */
    public static OSClient clientFromAccess(YaoAccess access, Facing perspective) {
        return OSClientSession.createSession((KeystoneAccess) access.getAccess(), perspective, null, null);
    }
    
}
