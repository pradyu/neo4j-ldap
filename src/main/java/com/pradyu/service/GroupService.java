package com.pradyu.service;

import com.pradyu.LdapConfig;
import com.pradyu.entity.Entity;
import com.pradyu.entity.Group;
import com.pradyu.entity.GroupLdap;
import com.pradyu.entity.LdapObject;
import com.pradyu.entity.User;
import com.pradyu.entity.UserLdap;
import com.pradyu.evaluator.UserEvaluator;
import com.pradyu.repo.EntityRepository;
import com.pradyu.repo.GroupRepository;
import com.pradyu.repo.UserRepository;
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

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    EntityRepository entityRepository;
    @Autowired
    LdapConfig ldapConfig;
    @Autowired
    LdapService ldapService;

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
                    if (!member.contains(ldapConfig.getBasePath())) {
                        continue;
                    }
                    logger.info("Could not get information about member - " + member);
                    List<LdapObject> ldapObjectList = ldapService.getObject(member);
                    for (LdapObject ldapObject : ldapObjectList) {
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
