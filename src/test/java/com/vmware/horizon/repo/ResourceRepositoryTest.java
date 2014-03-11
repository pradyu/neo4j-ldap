package com.vmware.horizon.repo;

import com.vmware.horizon.ApplicationConfig;
import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.Resource;
import com.vmware.horizon.entity.User;
import com.vmware.horizon.evaluator.ResourceEvaluator;
import com.vmware.horizon.evaluator.UserEvaluator;
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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
public class ResourceRepositoryTest {

    @Autowired private GroupRepository groupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ResourceRepository resourceRepository;

    Group group;
    User user1, user2;
    Resource resource;

    @Before
    public void setup() {
        group = new Group("parent");
        user1 = new User("user1");
        user2 = new User("user2");

        userRepository.save(user1);
        userRepository.save(user2);
        group.addMember(user2);
        groupRepository.save(group);
        resource = new Resource("app");
        resource.addEntitlement(user1);
        resource.addEntitlement(group);
        resourceRepository.save(resource);
    }

    @Test
    @Transactional
    public void canGetResourceWithEntitlements() {

        Resource savedResource = resourceRepository.findByCanonicalName(resource.getCanonicalName());
        assertNotNull(savedResource);
        assertTrue(savedResource.getEntitlements().size() == 2);
    }

    @Test
    @Transactional
    public void canFindAllUsersEntitledToResource() {

        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                .breadthFirst()
                .relationships(DynamicRelationshipType.withName("MEMBER"))
                .relationships(DynamicRelationshipType.withName("ENTITLED"))
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new UserEvaluator())
                .evaluator(Evaluators.all());

        Iterable<User> users = userRepository.findAllByTraversal(resource, traversalDescription);
        Iterator<User> iterator = users.iterator();
        ArrayList<User> allUsers = new ArrayList<User>();
        while (iterator.hasNext()) {
            allUsers.add(iterator.next());
        }

        org.junit.Assert.assertTrue(allUsers.size() == 2);

    }

    @Test
    @Transactional
    public void canFindUsersDirectlyEntitledToResource() {

        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                .breadthFirst()
                .relationships(DynamicRelationshipType.withName("ENTITLED"))
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new UserEvaluator())
                .evaluator(Evaluators.all());

        Iterable<User> users = userRepository.findAllByTraversal(resource, traversalDescription);
        Iterator<User> iterator = users.iterator();
        ArrayList<User> allUsers = new ArrayList<User>();
        while (iterator.hasNext()) {
            allUsers.add(iterator.next());
        }

        org.junit.Assert.assertTrue(allUsers.size() == 1);
        assertEquals(user1.getName(), allUsers.get(0).getName());

    }

    @Test
    @Transactional
    public void canGetAllResourcesEntitledToUser() {
        Resource newResource = new Resource("app2");
        newResource.addEntitlement(user1);
        resourceRepository.save(newResource);

        TraversalDescription traversalDescription = new TraversalDescriptionImpl()
                .breadthFirst()
                .relationships(DynamicRelationshipType.withName("ENTITLED"))
                .relationships(DynamicRelationshipType.withName("MEMBER"))
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new ResourceEvaluator())
                .evaluator(Evaluators.all());

        Iterable<Resource> resources = resourceRepository.findAllByTraversal(user1, traversalDescription);

        Iterator<Resource> iterator = resources.iterator();
        ArrayList<Resource> allResources = new ArrayList<Resource>();
        while (iterator.hasNext()) {
            allResources.add(iterator.next());
        }

        org.junit.Assert.assertTrue(allResources.size() == 2);


    }
}
