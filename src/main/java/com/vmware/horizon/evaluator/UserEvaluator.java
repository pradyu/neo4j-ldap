package com.vmware.horizon.evaluator;

import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.User;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class UserEvaluator implements Evaluator {
    @Override
    public Evaluation evaluate(Path propertyContainers) {
        Node n = propertyContainers.endNode();
        if (n.hasProperty("__type__") && (n.getProperty("__type__").equals(User.class.getCanonicalName()))) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        } else if (n.hasProperty("__type__") && (n.getProperty("__type__").equals(Group.class.getCanonicalName()))) {
            return Evaluation.EXCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }
}
