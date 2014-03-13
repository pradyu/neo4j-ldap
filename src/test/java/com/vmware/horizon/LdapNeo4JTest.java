package com.vmware.horizon;

import com.pradyu.ApplicationConfig;
import com.pradyu.LdapConfig;
import com.pradyu.entity.GroupLdap;
import com.pradyu.entity.User;
import com.pradyu.entity.UserLdap;
import com.pradyu.repo.UserRepository;
import com.pradyu.service.GroupService;
import com.pradyu.service.LdapService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
public class LdapNeo4JTest {

    private static Logger logger = Logger.getLogger(LdapNeo4JTest.class.getName());
    @Autowired private LdapService ldapService;
    @Autowired private UserRepository userRepository;
    @Autowired private LdapConfig ldapConfig;
    @Autowired private GroupService groupService;

    @Before
    public void before() throws Exception {
        ldapConfig.setupConnection();
    }


    @Test
    public void canAddUsersFromLdapToGraph() {
        HashSet<UserLdap> ldapUsers = ldapService.getAllUsers();
        ArrayList<User> users = new ArrayList<User>();

        for (UserLdap ldapUser: ldapUsers) {
            User user = new User(ldapUser.getCanonicalName().toLowerCase(), ldapUser.getUserPrincipalName());
            logger.info(ldapUser.getCanonicalName());
            user.setSelectedUser(true);
            users.add(user);
        }

        userRepository.save(users);
        logger.info("Total number of users: " + users.size());
        HashSet<GroupLdap> ldapGroups = ldapService.getAllGroups();
        for (GroupLdap ldapGroup: ldapGroups) {
            groupService.resolveMemberships(ldapGroup);
        }

        logger.info("Total number of users: " + users.size());
        logger.info("Total number of groups: " + ldapGroups.size());
    }
}
