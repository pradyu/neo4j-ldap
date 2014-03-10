package com.vmware.horizon.service;

import com.google.common.collect.Sets;
import com.vmware.horizon.Util.ObjectGuidConverter;
import com.vmware.horizon.entity.GroupLdap;
import com.vmware.horizon.entity.LdapObject;
import com.vmware.horizon.entity.UserLdap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;
import java.util.HashSet;
import java.util.List;

@Service
public class LdapService {

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired LdapContext ldapContext;

    @Autowired
    ObjectGuidConverter objectGuidConverter;


    public HashSet<UserLdap> getAllUsers() {
        PagedResultsDirContextProcessor contextProcessor = new PagedResultsDirContextProcessor(1000);

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        HashSet<UserLdap> users = new HashSet<UserLdap>();

        do {
            String filter = "(objectCategory=user)";
            users.addAll(ldapTemplate.search(ldapContext.searchDN(), filter, searchControls, new UserContextMapper(), contextProcessor));
        } while (contextProcessor.hasMore());
        return users;
    }

    public HashSet<GroupLdap> getAllGroups() {
        PagedResultsDirContextProcessor contextProcessor = new PagedResultsDirContextProcessor(1000);

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        HashSet<GroupLdap> groups = new HashSet<GroupLdap>();

        do {
            String filter = "(objectclass=group)";
            groups.addAll(ldapTemplate.search(ldapContext.searchDN(), filter, searchControls, new GroupContextMapper(), contextProcessor));
        } while (contextProcessor.hasMore());
        return groups;
    }

    public List<LdapObject> getObject(String dn) {
        String filter = "(objectclass=*)";
        String remainingName = getRemainingName(dn);
        return ldapTemplate.search(remainingName, filter, new LdapObjectContextMapper());
    }

    public String getRemainingName(String dn) {
        dn = dn.replace(ldapContext.getBasePath(), "");
        if (dn.length() > 0 && dn.charAt(dn.length()-1)==',') {
            dn = dn.substring(0, dn.length()-1);
        }
        return dn;
    }

    private class LdapObjectContextMapper extends AbstractContextMapper {
        @Override
        public Object doMapFromContext(DirContextOperations context) {
            String[] objectClasses = context.getStringAttributes("objectClass");
            boolean isGroup = false;
            for (String objectClass : objectClasses) {
                if ("group".equals(objectClass)) {
                    isGroup = true;
                    break;
                }
            }
            if (isGroup) {
                return new GroupContextMapper().doMapFromContext(context);
            }
            else {
                return new UserContextMapper().doMapFromContext(context);
            }
        }
    }


    private class GroupContextMapper extends AbstractContextMapper<GroupLdap> {
        @Override
        public GroupLdap doMapFromContext(DirContextOperations context) {
            GroupLdap group = new GroupLdap();
            group.setName(context.getStringAttribute("cn"));
            group.setCanonicalName(context.getDn().toString() + "," + ldapContext.getBasePath());
            if (context.attributeExists("memberOf")) {
                group.setMemberOf(Sets.newHashSet(context.getStringAttributes("memberOf")));
            }
            if (context.attributeExists("member")) {
                group.setMember(Sets.newHashSet(context.getStringAttributes("member")));
            }
            return group;
        }
    }

    private class UserContextMapper extends AbstractContextMapper<UserLdap> {
        @Override
        public UserLdap doMapFromContext(DirContextOperations context) {
            UserLdap user = new UserLdap();
            user.setCanonicalName(context.getDn().toString() + "," + ldapContext.getBasePath());
            if (context.attributeExists("userPrincipalName")) {
                user.setUserPrincipalName(context.getStringAttribute("userPrincipalName"));
            }
            return user;
        }
    }
}
