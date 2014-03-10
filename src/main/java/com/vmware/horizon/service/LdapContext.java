package com.vmware.horizon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.MutablePoolingContextSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class LdapContext {

    @Autowired
    LdapTemplate ldapTemplate;

    public void setupConnection() throws Exception {

        LdapContextSource cs = new LdapContextSource();

        //horizon730(cs);
        kdc(cs);
        cs.setReferral("follow");
        cs.afterPropertiesSet();
        MutablePoolingContextSource mutablePoolingContextSource = new MutablePoolingContextSource();
        mutablePoolingContextSource.setContextSource(cs);
        ldapTemplate.setContextSource(cs);
        ldapTemplate.afterPropertiesSet();
    }

    private void kdc(LdapContextSource cs) {
        cs.setUrl("ldap://kdc.hs.trcint.com");
        cs.setBase(getBasePath());
        cs.setUserDn("cn=devadmin,cn=Users,DC=hs,DC=trcint,DC=com");
        cs.setPassword("Ssn123456");
    }

    public String getBasePath() {
        //return "OU=Prad OU,DC=hs,DC=trcint,DC=com";
        return "cn=Users,DC=hs,DC=trcint,DC=com";
    }

    public String searchDN() {
        //return "CN=Users";
        return "";
    }
}
