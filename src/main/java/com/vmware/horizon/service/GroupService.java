package com.vmware.horizon.service;

import com.vmware.horizon.entity.Entity;
import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.GroupLdap;
import com.vmware.horizon.entity.LdapObject;
import com.vmware.horizon.entity.User;
import com.vmware.horizon.entity.UserLdap;
import com.vmware.horizon.evaluator.UserEvaluator;
import com.vmware.horizon.repo.EntityRepository;
import com.vmware.horizon.repo.GroupRepository;
import com.vmware.horizon.repo.UserRepository;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.impl.traversal.TraversalDescriptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class GroupService {

    Logger logger = Logger.getLogger(GroupService.class.getName());

    @Autowired UserRepository userRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired EntityRepository entityRepository;
    @Autowired LdapContext ldapContext;
    @Autowired LdapService ldapService;

    public Iterable<User> traverseAllUsersFromGroup(Group group) {
        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                .breadthFirst()
                .relationships(DynamicRelationshipType.withName("MEMBER"))
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new UserEvaluator())
                .evaluator(Evaluators.all());

        return userRepository.findAllByTraversal(group, traversalDescription);
    }

    public void resolveMemberships(GroupLdap ldapGroup) {
        Group group = groupRepository.findByCanonicalName(ldapGroup.getCanonicalName().toLowerCase());
        if (group == null) {
            group = new Group(ldapGroup.getCanonicalName().toLowerCase(), ldapGroup.getCanonicalName());
            //groupRepository.save(group);
        }

        Set<String> members = ldapGroup.getMember();
        if (members != null) {
            for (String member : members) {
                Entity entity = entityRepository.findByCanonicalName(member.toLowerCase());
                if (entity != null) {
                    group.addMember(entity);
                } else {
                    if (! member.contains(ldapContext.getBasePath())) {
                        continue;
                    }
                    logger.info("Could not get information about member - " + member);
                    List<LdapObject> ldapObjectList = ldapService.getObject(member);
                    for (LdapObject ldapObject: ldapObjectList) {
                        if (ldapObject instanceof UserLdap) {
                            // Add user
                            UserLdap ldapUser = (UserLdap) ldapObject;
                            User user = new User(ldapUser.getCanonicalName().toLowerCase(), ldapUser.getUserPrincipalName());
                            userRepository.save(user);
                            group.addMember(user);
                        }
                        if (ldapObject instanceof GroupLdap) {
                            GroupLdap memberGroup = (GroupLdap) ldapObject;
                            // Save the group before nesting
                            group.addMember(group);
                            groupRepository.save(group);
                            resolveMemberships(memberGroup);
                        }
                    }
                }
            }
            groupRepository.save(group);
        }
    }

}
