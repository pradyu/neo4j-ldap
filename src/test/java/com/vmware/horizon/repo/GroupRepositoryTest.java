package com.vmware.horizon.repo;

import com.vmware.horizon.ApplicationConfig;
import com.vmware.horizon.entity.Entity;
import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.User;
import com.vmware.horizon.service.LdapContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.impl.traversal.TraversalDescriptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
public class GroupRepositoryTest {

    @Autowired private GroupRepository groupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LdapContext ldapContext;

    ArrayList<Entity> entities = new ArrayList<Entity>();

    @Before
    public void before() throws Exception {
        ldapContext.setupConnection();
    }


    @Test
    @Transactional
    public void canCreateGroup() {
        Group parent = new Group("parent");
        Group member = new Group("member");

        groupRepository.save(parent);
        groupRepository.save(member);

        entities.add(parent);
        entities.add(member);

        parent = groupRepository.findByName(parent.getName());
        assertNotNull(parent.getMembers());
        assertTrue(parent.getMembers().size() == 0);

        parent.addMember(member);

        groupRepository.save(parent);

        parent = groupRepository.findByName(parent.getName());
        assertNotNull(parent.getMembers());
        assertTrue(parent.getMembers().size() == 1);

    }

    @Test
    @Transactional
    public void canAddMembers() {
        Group parent = new Group("parent");
        parent = groupRepository.save(parent);
        entities.add(parent);
        assertNotNull(parent);

        User user = new User("user");
        userRepository.save(user);
        entities.add(user);

        parent.addMember(user);
        parent = groupRepository.save(parent);
        assertNotNull(parent.getMembers());
        assertTrue(parent.getMembers().size() == 1);

        Group child = new Group("member");
        parent.addMember(child);
        parent = groupRepository.save(parent);
        entities.add(child);
        assertTrue(parent.getMembers().size() == 2);
    }

    @Test
    @Transactional
    public void canTravereAllMembers() {
        Group parent = new Group("parent");
        Group child = new Group("child");
        Group subChild = new Group("subChild");
        Group subChild2 = new Group("subChild2");

        parent = groupRepository.save(parent);
        child = groupRepository.save(child);
        subChild = groupRepository.save(subChild);
        subChild2 = groupRepository.save(subChild2);

        parent.addMember(child);
        groupRepository.save(parent);

        child.addMember(subChild);
        child.addMember(subChild2);
        groupRepository.save(child);

        parent = groupRepository.findByName(parent.getName());

        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                                                    .breadthFirst()
                                                    .relationships(DynamicRelationshipType.withName("MEMBER"))
                                                    .evaluator(Evaluators.excludeStartPosition())
                                                    .evaluator(Evaluators.all());

        Iterable<Group> groups = groupRepository.findAllByTraversal(parent, traversalDescription);
        Iterator<Group> iterator = groups.iterator();
        ArrayList<Group> allGroups = new ArrayList<Group>();
        while (iterator.hasNext()) {
            allGroups.add(iterator.next());
        }

        assertTrue(allGroups.size() == 3);
    }
}
