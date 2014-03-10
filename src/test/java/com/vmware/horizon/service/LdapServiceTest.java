package com.vmware.horizon.service;

import com.vmware.horizon.ApplicationConfig;
import com.vmware.horizon.entity.GroupLdap;
import com.vmware.horizon.entity.UserLdap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
public class LdapServiceTest {

    @Autowired private LdapService ldapService;
    @Autowired private LdapContext ldapContext;

    @Before
    public void before() throws Exception {
        ldapContext.setupConnection();
    }

    @Test
    public void canGetUsers() {
        HashSet<UserLdap> users = ldapService.getAllUsers();
        assertTrue(users.size() > 0);
    }

    @Test
    public void canGetGroups() {
        HashSet<GroupLdap> groups = ldapService.getAllGroups();
        assertTrue(groups.size() > 0);
    }

}
