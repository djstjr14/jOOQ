package org.jooq.checker;

import static com.sun.source.util.TreePath.getPath;
import static java.util.Arrays.asList;
import static org.checkerframework.javacutil.TreeUtils.elementFromDeclaration;
import static org.checkerframework.javacutil.TreeUtils.elementFromUse;
import static org.checkerframework.javacutil.TreeUtils.enclosingMethod;

import java.util.EnumSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.jooq.Allow;
import org.jooq.Require;
import org.jooq.SQLDialect;
import org.jooq.Support;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;

public class SupportAnnotationCheck implements MethodInvocation {

	@Override
	public String checker(MethodInvocationTree node, Void p, CompilationUnitTree root) {

		ExecutableElement elementFromUse = elementFromUse(node);
		Support support = elementFromUse.getAnnotation(Support.class);

		// In the absence of a @Support annotation, or if no SQLDialect is supplied,
		// all jOOQ API method calls will type check.
		if (support != null && support.value().length > 0) {
			Element enclosing = elementFromDeclaration(enclosingMethod(getPath(root, node)));

			EnumSet<SQLDialect> supported = EnumSet.copyOf(asList(support.value()));
			EnumSet<SQLDialect> allowed = EnumSet.noneOf(SQLDialect.class);
			EnumSet<SQLDialect> required = EnumSet.noneOf(SQLDialect.class);

			boolean evaluateRequire = true;
			while (enclosing != null) {
				Allow allow = enclosing.getAnnotation(Allow.class);

				if (allow != null)
					allowed.addAll(asList(allow.value()));

				if (evaluateRequire) {
					Require require = enclosing.getAnnotation(Require.class);

					if (require != null) {
						evaluateRequire = false;

						required.clear();
						required.addAll(asList(require.value()));
					}
				}

				enclosing = enclosing.getEnclosingElement();
			}

			if (allowed.isEmpty())
				return "No jOOQ API usage is allowed at current scope. Use @Allow.";
			if (required.isEmpty())
				return "No jOOQ API usage is allowed at current scope due to conflicting @Require specification.";
			boolean allowedFail = true;
			allowedLoop:
				for (SQLDialect a : allowed) {
					for (SQLDialect s : supported) {
						if (a.supports(s)) {
							allowedFail = false;
							break allowedLoop;
						}
					}
				}

			if (allowedFail)
				return "The allowed dialects in scope " + allowed + " do not include any of the supported dialects: " + supported;

			boolean requiredFail = false;
			requiredLoop:
				for (SQLDialect r : required) {
					for (SQLDialect s : supported)
						if (r.supports(s))
							continue requiredLoop;

					requiredFail = true;
					break requiredLoop;
				}

			if (requiredFail)
				return "Not all of the required dialects " + required + " from the current scope are supported " + supported;

		}
		return null;

	}
}
