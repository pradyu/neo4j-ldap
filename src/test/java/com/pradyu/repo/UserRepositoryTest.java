package com.pradyu.repo;

import com.pradyu.ApplicationConfig;
import com.pradyu.entity.Group;
import com.pradyu.entity.User;
import com.pradyu.evaluator.UserEvaluator;
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
public class UserRepositoryTest {

    @Autowired private GroupRepository groupRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @Transactional
    public void canAddMembers() {
        Group parent = new Group("parent");
        parent = groupRepository.save(parent);
        assertNotNull(parent);

        User user = new User("user");
        userRepository.save(user);

        parent.addMember(user);
        parent = groupRepository.save(parent);
        assertNotNull(parent.getMembers());
        assertTrue(parent.getMembers().size() == 1);

        Group child = new Group("member");
        parent.addMember(child);
        parent = groupRepository.save(parent);
        assertTrue(parent.getMembers().size() == 2);
    }

    @Test
    @Transactional
    public void canTraverseUsersInGroup() {
        Group parent = new Group("parent");
        Group child = new Group("child");
        Group subChild = new Group("subChild");
        User user = new User("user");

        parent = groupRepository.save(parent);
        child = groupRepository.save(child);
        subChild = groupRepository.save(subChild);
        user = userRepository.save(user);

        parent.addMember(child);
        groupRepository.save(parent);

        child.addMember(subChild);
        child.addMember(user);

        groupRepository.save(child);

        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                .breadthFirst()
                .relationships(DynamicRelationshipType.withName("MEMBER"))
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new UserEvaluator())
                .evaluator(Evaluators.all());

        Iterable<User> users = userRepository.findAllByTraversal(parent, traversalDescription);

        Iterator<User> iterator = users.iterator();
        ArrayList<User> allUsers = new ArrayList<User>();
        while (iterator.hasNext()) {
            allUsers.add(iterator.next());
        }

        assertTrue(allUsers.size() == 1);
    }
}
