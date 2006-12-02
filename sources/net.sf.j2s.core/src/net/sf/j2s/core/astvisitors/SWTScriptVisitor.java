/*******************************************************************************
 * Copyright (c) 2005 ognize.com and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ognize.com - initial API and implementation
 *******************************************************************************/
package net.sf.j2s.core.astvisitors;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class SWTScriptVisitor extends ASTScriptVisitor {

	private boolean metSWTBlockWhile = false;
	private boolean metDialogOpen = false;

	public SWTScriptVisitor() {
		super();
	}

	public SWTScriptVisitor(boolean visitDocTags) {
		super(visitDocTags);
	}

	/* (non-Javadoc)
	 * @see net.sf.j2s.core.astvisitors.ASTKeywordParser#skipDeclarePackages()
	 */
	protected String[] skipDeclarePackages() {
		String swt = "org.eclipse.swt";
		String[] swtInnerPackages = new String[]{
				swt, 
				swt + ".accessibility", 
				swt + ".browser", 
				swt + ".custom", 
				swt + ".dnd",
				swt + ".events", 
				swt + ".graphics", 
				swt + ".internal", 
				swt + ".internal.dnd", 
				swt + ".internal.browser", 
				swt + ".internal.struct", 
				swt + ".layout", 
				swt + ".widgets"
		};
		String[] pkgs = super.skipDeclarePackages();
		String[] packages = new String[swtInnerPackages.length + pkgs.length];
		System.arraycopy(pkgs, 0, packages, 0, pkgs.length);
		System.arraycopy(swtInnerPackages, 0, packages, pkgs.length, swtInnerPackages.length);
		return packages;
	}
	
	public boolean visit(SimpleName node) {
		if (checkConstantValue(node)) return false;
		IBinding binding = node.resolveBinding();
		if (binding != null
				&& binding instanceof ITypeBinding) {
			ITypeBinding typeBinding = (ITypeBinding) binding;
			if (typeBinding != null) {
				String name = typeBinding.getQualifiedName();
				if (name.startsWith("org.eclipse.swt.internal.xhtml")) {
					String identifier = node.getIdentifier();
					if ("window".equals(identifier)) {
						identifier = "w$";
					} else if ("document".equals(identifier)) {
						identifier = "d$";
					}
					buffer.append(identifier);
					return false;
				}
				if ("org.eclipse.swt.internal.browser.OS".equals(name)) {
					buffer.append("O$");
					return false;
				}
			}
		}
		return super.visit(node);
	}
	public boolean visit(QualifiedName node) {
		if (isSimpleQualified(node) && checkConstantValue(node)) return false;
		ASTNode parent = node.getParent();
		if (parent != null && !(parent instanceof QualifiedName)) {
			Name qualifier = node.getQualifier();
			while (qualifier instanceof QualifiedName) {
				IBinding binding = qualifier.resolveBinding();
				if (binding != null && !(binding instanceof IVariableBinding)) {
					Name xqualifier = ((QualifiedName) qualifier).getQualifier();
					if (xqualifier instanceof QualifiedName) {
						IBinding xbinding = qualifier.resolveBinding();
						if (xbinding != null && !(xbinding instanceof IVariableBinding)) {
							qualifier = xqualifier;
							continue;
						}
					}
				}
				break;
			}
			IBinding binding = qualifier.resolveBinding();
			if (binding != null) {
				if (!(binding instanceof IVariableBinding)) {
					ITypeBinding typeBinding = qualifier.resolveTypeBinding();
					if (typeBinding != null) {
						String name = null;
						ITypeBinding declaringClass = typeBinding.getDeclaringClass();
						if (declaringClass != null) {
							name = declaringClass.getQualifiedName();
						} else {
							name = "";
						}
						name = JavaLangUtil.ripJavaLang(name);
						if (name.indexOf("java.lang") != -1) {
							name = name.substring(9);
						}
						String xhtml = "org.eclipse.swt.internal.xhtml";
						if (name.indexOf(xhtml) == 0) {
							name = name.substring(xhtml.length());
						}
						xhtml = "$wt.internal.xhtml";
						if (name.indexOf(xhtml) == 0) {
							name = name.substring(xhtml.length());
						}
						if ("window".equals(name)) {
							name = "w$";
						} else if ("document".equals(name)) {
							name = "d$";
						}
						if (name.length() != 0) {
							buffer.append(name);
							buffer.append('.');
						}
					}
				}
			}
		}
		node.getQualifier().accept(this);
		buffer.append('.');
		node.getName().accept(this);
		return false;
	}
	public boolean visit(ClassInstanceCreation node) {
		ITypeBinding binding = node.resolveTypeBinding();
		if (binding != null) {
			if (isTypeOf(binding, "org.eclipse.swt.internal.RunnableCompatibility")) {
				buffer.append("Clazz.makeFunction (");
				boolean result = super.visit(node);
				buffer.append(")");
				return result;
			}
		}
		AnonymousClassDeclaration anonDeclare = node.getAnonymousClassDeclaration();
		if (anonDeclare != null) {
		} else {
			String fqName = null;
			if (node.getAST().apiLevel() != AST.JLS3) {
				fqName = node.getName().getFullyQualifiedName();
			} else {
				String name = ASTJL3.getTypeStringName(node.getType());
				if (name != null) {
					fqName = name;//.getFullyQualifiedName();
				} else {
					fqName = "noname";
				}
			}
			fqName = JavaLangUtil.ripJavaLang(fqName);
			String filterKey = "org.eclipse.swt.internal.xhtml.";
			if (fqName.startsWith(filterKey)) {
				buffer.append(" new ");
				buffer.append(fqName.substring(filterKey.length()));
				buffer.append(" (");
				visitList(node.arguments(), ", ");
				buffer.append(")");
				return false;
			}
			filterKey = "$wt.internal.xhtml.";
			if (fqName.startsWith(filterKey)) {
				buffer.append(" new ");
				buffer.append(fqName.substring(filterKey.length()));
				buffer.append(" (");
				visitList(node.arguments(), ", ");
				buffer.append(")");
				return false;
			}
		}
		return super.visit(node);
	}
	
	boolean isTypeOf(ITypeBinding binding, String clazzName) {
		if (binding == null || clazzName == null || clazzName.length() == 0) {
			return false;
		}
		if (clazzName.equals(binding.getBinaryName())) {
			return true;
		} else {
			return isTypeOf(binding.getSuperclass(), clazzName);
		}
	}
	public boolean visit(WhileStatement node) {
		Expression exp = node.getExpression();
		if (exp instanceof PrefixExpression) {
			PrefixExpression preExp = (PrefixExpression) exp;
			if ("!".equals(preExp.getOperator().toString())) {
				Expression operand = preExp.getOperand();
				if (operand instanceof MethodInvocation) {
					MethodInvocation shellIsDisposed = (MethodInvocation) operand;
					Expression shellExp = shellIsDisposed.getExpression();
					if (shellExp != null) {
						ITypeBinding typeBinding = shellExp.resolveTypeBinding();
						if (isTypeOf(typeBinding, "org.eclipse.swt.widgets.Shell")) {
							SimpleName methodName = shellIsDisposed.getName();
							if ("isDisposed".equals(methodName.getIdentifier())) {
								metSWTBlockWhile = true;
								buffer.append("Sync2Async.block (");
								shellExp.accept(this);
								buffer.append(", this, function () {\r\n");
								return false;
							}
						}
					}
				}
			}
		}
		return super.visit(node);
	}
	
	public void endVisit(Block node) {
		super.endVisit(node);
	}

	protected String[] getFilterMethods() {
		return new String[] {
				"org.eclipse.swt.widgets.Widget", "checkSubclass",
				"org.eclipse.swt.widgets.Dialog", "checkSubclass",
				"org.eclipse.swt.widgets.Widget", "checkWidget",
				"org.eclipse.swt.widgets.Display", "checkDevice",
				"org.eclipse.swt.graphics.Device", "checkDevice"
		};
	}
	/* (non-Javadoc)
	 * @see net.sf.j2s.core.astvisitors.ASTScriptVisitor#visit(org.eclipse.jdt.core.dom.MethodInvocation)
	 */
	public boolean visit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if ("open".equals(methodBinding.getName()) && methodBinding.getParameterTypes().length == 0) {
			if (Bindings.findTypeInHierarchy(methodBinding.getDeclaringClass(), "org.eclipse.swt.widgets.Dialog") != null) {
				int lastIndexOf1 = buffer.lastIndexOf(";\r\n");
				if (lastIndexOf1 != -1) {
					lastIndexOf1 += 3;
				}
				int lastIndexOf2 = buffer.lastIndexOf("}\r\n");
				if (lastIndexOf2 != -1) {
					lastIndexOf2 += 3;
				}
				int lastIndexOf3 = buffer.lastIndexOf("}");
				if (lastIndexOf3 != -1) {
					lastIndexOf3 += 1;
				}
				int lastIndexOf4 = buffer.lastIndexOf("{\r\n");
				if (lastIndexOf4 != -1) {
					lastIndexOf4 += 3;
				}
				int lastIndexOf5 = buffer.lastIndexOf("{");
				if (lastIndexOf5 != -1) {
					lastIndexOf5 += 1;
				}
				int lastIndexOf = -1;
				if (lastIndexOf1 == -1 && lastIndexOf2 == -1 
						&& lastIndexOf3 == -1 && lastIndexOf1 == -1 
						&& lastIndexOf2 == -1 && lastIndexOf3 == -1) {
					lastIndexOf = buffer.length(); // should never be in here!
				} else {
					lastIndexOf = Math.max(Math.max(Math.max(lastIndexOf1, lastIndexOf2), lastIndexOf3), 
							Math.max(lastIndexOf4, lastIndexOf5)); 
				}
				String s = buffer.substring(lastIndexOf);
				buffer.delete(lastIndexOf, buffer.length());
				buffer.append("DialogSync2Async.block (");
				node.getExpression().accept(this);
				buffer.append(", this, function () {\r\n");
				buffer.append(s);
				node.getExpression().accept(this);
				buffer.append(".dialogReturn");
				metDialogOpen = true;
				return false;
			}
		}
		if ("net.sf.j2s.ajax.junit.AsyncSWT".equals(methodBinding.getDeclaringClass().getQualifiedName())
				&& "waitLayout".equals(methodBinding.getName())) {
			metSWTBlockWhile = true;
			node.getExpression().accept(this);
			buffer.append(".waitLayout (");
			Expression shellExp = (Expression) node.arguments().get(0);
			shellExp.accept(this);
			buffer.append(", ");
			Expression runnableExp = (Expression) node.arguments().get(1);
			runnableExp.accept(this);
			buffer.append(", this, function () {\r\n//");
			return false;
		}
		String[] filterMethods = getFilterMethods();
		for (int i = 0; i < filterMethods.length; i += 2) {
			if (isMethodInvoking(methodBinding, filterMethods[i], filterMethods[i + 1])) {
				return false;
			}
		}
		return super.visit(node);
	}
	/* (non-Javadoc)
	 * @see net.sf.j2s.core.astvisitors.ASTScriptVisitor#endVisit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public void endVisit(MethodDeclaration node) {
		IMethodBinding methodBinding = node.resolveBinding();
		String[] filterMethods = getFilterMethods();
		for (int i = 0; i < filterMethods.length; i += 2) {
			if (isMethodInvoking(methodBinding, filterMethods[i], filterMethods[i + 1])) {
				return ;
			}
		}
		super.endVisit(node);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.j2s.core.astvisitors.ASTScriptVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public boolean visit(MethodDeclaration node) {
		IMethodBinding methodBinding = node.resolveBinding();
		String[] filterMethods = getFilterMethods();
		for (int i = 0; i < filterMethods.length; i += 2) {
			if (isMethodInvoking(methodBinding, filterMethods[i], filterMethods[i + 1])) {
				return false;
			}
		}
		return super.visit(node);
	}
	
	public boolean visit(Block node) {
		int swtBlockWhileCount = 0;
		int swtDialogOpenCount = 0;
		boolean lastSWTBlockWhile = metSWTBlockWhile;
		metSWTBlockWhile = false;
		boolean lastDialogOpen = metDialogOpen;
		metDialogOpen = false;
		if (super.visit(node) == false) {
			metSWTBlockWhile = lastSWTBlockWhile;
			metDialogOpen = lastDialogOpen;
			return false;
		}
		List statements = node.statements();
		for (Iterator iter = statements.iterator(); iter.hasNext();) {
			Statement stmt = (Statement) iter.next();
			if (stmt instanceof ExpressionStatement) {
				ExpressionStatement expStmt = (ExpressionStatement) stmt;
				Expression exp = expStmt.getExpression();
				String[] filterMethods = getFilterMethods();
				boolean isContinue = false;
				for (int i = 0; i < filterMethods.length; i += 2) {
					if (isMethodInvoking(exp, filterMethods[i], filterMethods[i + 1])) {
						isContinue = true;
						break;
					}
				}
				if (isContinue) {
					continue;
				}
			}
			stmt.accept(this);
			if (metSWTBlockWhile) {
				swtBlockWhileCount++;
				metSWTBlockWhile = false;
			}
			if (metDialogOpen) {
				swtDialogOpenCount++;
				metDialogOpen = false;
			}
		}
		for (int i = 0; i < swtBlockWhileCount + swtDialogOpenCount; i++) {
			buffer.append("});\r\n");
			buffer.append("return;\r\n"); /* always return directly when dialog#open is called */
		}
		metSWTBlockWhile = lastSWTBlockWhile;
		metDialogOpen = lastDialogOpen;
		return false;
	}
	
	private boolean isMethodInvoking(IMethodBinding methodBinding, String className, String methodName) {
		if (methodName.equals(methodBinding.getName())) {
			IMethodBinding findMethodInHierarchy = Bindings.findMethodInHierarchy(methodBinding.getDeclaringClass(), methodName, null);
			IMethodBinding last = findMethodInHierarchy;
			int count = 0;
			while (findMethodInHierarchy != null && (count++) < 10) {
				last = findMethodInHierarchy;
				ITypeBinding superclass = last.getDeclaringClass().getSuperclass();
				if (superclass == null) {
					break;
				}
				findMethodInHierarchy = 
					Bindings.findMethodInHierarchy(superclass, methodName, null);
			}
			if (last == null) {
				last = methodBinding;
			}
			if (className.equals(last.getDeclaringClass().getQualifiedName())) {
				return true;
			}
		}
		return false;
	}
	private boolean isMethodInvoking(Expression exp, String className, String methodName) {
		if (exp instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) exp;
			IMethodBinding methodBinding = method.resolveMethodBinding();
			if (isMethodInvoking(methodBinding, className, methodName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean visit(IfStatement node) {
		if (node.getElseStatement() == null) {
			Statement thenStatement = node.getThenStatement();
			if (thenStatement instanceof Block) {
				Block block = (Block) thenStatement;
				List statements = block.statements();
				if (statements.size() == 1) {
					thenStatement = (Statement) statements.get(0);
				}
			}
			if (thenStatement instanceof ExpressionStatement) {
				ExpressionStatement expStmt = (ExpressionStatement) thenStatement;
				Expression exp = expStmt.getExpression();
				if (isMethodInvoking(exp, "org.eclipse.swt.widgets.Widget", "error")) {
					return false;
				}
				if (isMethodInvoking(exp, "org.eclipse.swt.SWT", "error")) {
					return false;
				}
				if (isMethodInvoking(exp, "org.eclipse.swt.widgets.Display", "error")) {
					return false;
				}
			}
		}
		return super.visit(node);
	}
}
