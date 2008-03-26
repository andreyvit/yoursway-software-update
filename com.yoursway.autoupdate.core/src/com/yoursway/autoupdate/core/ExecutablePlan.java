package com.yoursway.autoupdate.core;

import static com.google.common.base.Predicates.isNull;
import static com.google.common.collect.Iterators.any;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class ExecutablePlan {
    
    private final List<Action> actions;

    public ExecutablePlan(List<Action> actions) {
        if (any(actions.iterator(), isNull()))
            throw new NullPointerException("The collection contains a null element");
        this.actions = newArrayList(actions);
    }

	public void execute(Executor executor) {
	    for (Action action : actions)
	        action.execute(executor);
	}

}
