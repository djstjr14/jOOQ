package org.jooq.checker;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import static com.sun.source.util.TreePath.getPath;
import static org.checkerframework.javacutil.TreeUtils.elementFromDeclaration;
import static org.checkerframework.javacutil.TreeUtils.elementFromUse;
import static org.checkerframework.javacutil.TreeUtils.enclosingMethod;
import org.jooq.Allow;
import org.jooq.PlainSQL;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;

public class PlainAnnoationChecker implements MethodInvocation{

	@Override
	public String checker(MethodInvocationTree node, Void p, CompilationUnitTree root) {
        ExecutableElement elementFromUse = elementFromUse(node);
        PlainSQL plainSQL = elementFromUse.getAnnotation(PlainSQL.class);

        // In the absence of a @PlainSQL annotation,
        // all jOOQ API method calls will type check.
        if (plainSQL != null) {
            Element enclosing = elementFromDeclaration(enclosingMethod(getPath(root, node)));
            boolean allowed = false;

            moveUpEnclosingLoop:
            while (enclosing != null) {
                if (enclosing.getAnnotation(Allow.PlainSQL.class) != null) {
                    allowed = true;
                    break moveUpEnclosingLoop;
                }
                enclosing = enclosing.getEnclosingElement();
            }
            if (!allowed)
                return "Plain SQL usage not allowed at current scope. Use @Allow.PlainSQL.";
        }
		return null;	
	}

}
