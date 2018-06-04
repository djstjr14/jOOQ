/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.checker;

import java.io.PrintWriter;
import org.jooq.PlainSQL;

import org.checkerframework.framework.source.SourceVisitor;

import com.sun.source.tree.MethodInvocationTree;

/**
 * A checker to disallow usage of {@link PlainSQL} API, except where allowed
 * explicitly.
 *
 * @author Lukas Eder
 */
public class PlainSQLChecker extends AbstractChecker {

	@Override
	protected SourceVisitor<Void, Void> createSourceVisitor() {
		return new SourceVisitor<Void, Void>(getChecker()) {

			@Override
			public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
				try {
					String str=methodinvocation.checker(node, p, root); // 수
					if (str!=null)
						error(node, str);
				}
				catch (final Exception e) {
					print(new Printer() {
						@Override
						public void print(PrintWriter t) {
							e.printStackTrace(t);
						}
					});
				}
				return super.visitMethodInvocation(node, p);
			}
		};
	}
}
