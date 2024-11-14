package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;

    public ValueExpr(Value v) {
        this.val = v;
    }

    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;

    public VarExpr(String varName) {
        this.varName = varName;
    }

    public Value evaluate(Environment env) {
        return env.resolveVar(this.varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;

    public PrintExpr(Expression exp) {
        this.exp = exp;
    }

    public Value evaluate(Environment env) {
        Value v = this.exp.evaluate(env);
        return v;
    }
}

/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;

    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        Value v1 = this.e1.evaluate(env);
        Value v2 = this.e2.evaluate(env);

        int x = ((IntVal) v1).toInt();
        int y = ((IntVal) v2).toInt();
        // ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, GT, GE, LT, LE, EQ
        switch (this.op) {
            case ADD:
                return new IntVal(x + y);
            case SUBTRACT:
                return new IntVal(x - y);
            case DIVIDE:
                return new IntVal(x / y);
            case MULTIPLY:
                return new IntVal(x * y);
            case MOD:
                return new IntVal(x % y);
            case GT:
                return new BoolVal(x > y);
            case GE:
                return new BoolVal(x >= y);
            case LT:
                return new BoolVal(x < y);
            case LE:
                return new BoolVal(x <= y);
            case EQ:
                return new BoolVal(x == y);
        }

        return null;
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;

    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }

    public Value evaluate(Environment env) {
        Value v1 = this.cond.evaluate(env);
        Value v2 = this.thn.evaluate(env);
        Value v3 = this.els.evaluate(env);

        if (((BoolVal) v1).toBoolean()) {
            return v2;
        } else {
            return v3;
        }

    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;

    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }

    public Value evaluate(Environment env) {
        while (((BoolVal) this.cond.evaluate(env)).toBoolean()) {
            this.body.evaluate(env);
        }
        return null;
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;

    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public Value evaluate(Environment env) {
        this.e1.evaluate(env);
        return this.e2.evaluate(env);
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;

    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }

    public Value evaluate(Environment env) {
        env.createVar(this.varName, this.exp.evaluate(env));
        return env.resolveVar(this.varName);
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;

    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }

    public Value evaluate(Environment env) {
        env.updateVar(this.varName, e.evaluate(env));
        return env.resolveVar(this.varName);
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;

    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }

    public Value evaluate(Environment env) {
        return new ClosureVal(this.params, this.body, env);
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;

    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }

    public Value evaluate(Environment env) {
        ClosureVal closure = (ClosureVal) this.f.evaluate(env);
        List<Value> argVals = new ArrayList<Value>();
        for (Expression arg : this.args) {
            argVals.add(arg.evaluate(env));
        }
        return closure.apply(argVals);
    }
}
