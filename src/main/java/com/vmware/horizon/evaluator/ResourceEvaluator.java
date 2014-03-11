package com.vmware.horizon.evaluator;

import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.Resource;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class ResourceEvaluator implements Evaluator {
    @Override
    public Evaluation evaluate(Path propertyContainers) {
        Node n = propertyContainers.endNode();
        if (n.hasProperty("__type__") && (n.getProperty("__type__").equals(Resource.class.getCanonicalName()))) {
            return Evaluation.INCLUDE_AND_PRUNE;
        } else if (n.hasProperty("__type__") && (n.getProperty("__type__").equals(Group.class.getCanonicalName()))) {
            return Evaluation.EXCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }
}
