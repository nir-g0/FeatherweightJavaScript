package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String, Value> env = new HashMap<String, Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {
    }

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {
        if (this.env.containsKey(varName)) {
            return this.env.get(varName);
        } else if (this.outerEnv != null) {
            return this.outerEnv.resolveVar(varName);
        } else {
            return new NullVal();
        }
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {
        // YOUR CODE HERE
        if (this.env.containsKey(key)) {
            this.env.replace(key, v);
        } else if (outerEnv != null) {
            this.outerEnv.updateVar(key, v);
        } else {
            this.createVar(key, v);
        }
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {
        // YOUR CODE HERE
        if (this.env.containsKey(key)) {
            throw new RuntimeException("Variable has been defined in the current scope already");
        } else {
            this.env.put(key, v);
        }
    }
}
