package org.jooq.checker;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;

public interface MethodInvocation {
	String checker(MethodInvocationTree node, Void p, CompilationUnitTree root);
}
