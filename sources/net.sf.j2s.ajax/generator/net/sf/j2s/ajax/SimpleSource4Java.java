/*******************************************************************************
 * Copyright (c) 2012 java2script.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zhou Renjian - initial API and implementation
 *******************************************************************************/

package net.sf.j2s.ajax;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.j2s.ajax.SimpleSerializable;

public class SimpleSource4Java {
	
	static String folder = "Project";
	static String author = "Author";
	static String company = "Company";

	@SuppressWarnings("deprecation")
	public static String generateHeaderFile(Class<?> interfaceClazz) {
		StringBuffer buffer = new StringBuffer();
		Date date = new Date();
		buffer.append("/**\r\n");
		buffer.append(" * Generated by Java2Script.\r\n");
		buffer.append(" * Copyright (c) ");
		buffer.append(date.getYear() + 1900);
		buffer.append(" ");
		buffer.append(company);
		buffer.append(". All rights reserved.\r\n");
		buffer.append(" */\r\n");
		buffer.append("\r\n");

		String clazzName = interfaceClazz.getName();
		String simpleClazzName = clazzName;
		int idx = clazzName.lastIndexOf('.');
		if (idx != -1) {
			buffer.append("package ");
			buffer.append(simpleClazzName.substring(0, idx));
			buffer.append(";\r\n");
			buffer.append("\r\n");
			
			simpleClazzName = clazzName.substring(idx + 1);
		}

		Class<?> superClazz = interfaceClazz.getSuperclass();
		if (superClazz != null) {
			String superClazzName = superClazz.getName();
			buffer.append("import ");
			buffer.append(superClazzName);
			buffer.append(";\r\n");
			buffer.append("\r\n");
			
			idx = superClazzName.lastIndexOf('.');
			if (idx != -1) {
				superClazzName = superClazzName.substring(idx + 1);
			}
			
			buffer.append("public interface ");
			buffer.append(simpleClazzName);
			buffer.append(" extends ");
			buffer.append(superClazzName);
			buffer.append(" {\r\n");
		} else {
			buffer.append("public interface ");
			buffer.append(simpleClazzName);
			buffer.append(" {\r\n");
		}
		buffer.append("\r\n");

		boolean gotStaticFinalFields = false;
		Field[] clazzFields = interfaceClazz.getDeclaredFields();
		
		for (int i = 0; i < clazzFields.length; i++) {
			Field f = clazzFields[i];
			int modifiers = f.getModifiers();
			if ((modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0
					&& (modifiers & Modifier.STATIC) != 0 && (modifiers & Modifier.FINAL) != 0) {
				Class<?> type = f.getType();
				if (type == int.class || type == long.class || type == short.class 
						|| type == byte.class || type == char.class || type == double.class
						|| type == float.class || type == boolean.class || type == String.class) {
					buffer.append("\tpublic ");
					buffer.append(type.getSimpleName());
					buffer.append(" ");
					buffer.append(f.getName());
					buffer.append(" = ");
					try {
						if (type == int.class) {
							buffer.append("" + f.getInt(interfaceClazz));
						} else if (type == long.class) {
							buffer.append(f.getLong(interfaceClazz) + "L");
						} else if (type == short.class) {
							buffer.append("" + f.getShort(interfaceClazz));
						} else if (type == byte.class) {
							buffer.append("" + f.getByte(interfaceClazz));
						} else if (type == char.class) {
							buffer.append("\'" + f.getChar(interfaceClazz) + "\'");
						} else if (type == float.class) {
							buffer.append("" + f.getFloat(interfaceClazz));
						} else if (type == double.class) {
							buffer.append("" + f.getDouble(interfaceClazz));
						} else if (type == boolean.class) {
							if (f.getBoolean(interfaceClazz)) {
								buffer.append("true");
							} else {
								buffer.append("false");
							}
						} else if (type == String.class) {
							buffer.append("\"" + f.get(interfaceClazz) + "\"");
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					buffer.append(";\r\n");
					gotStaticFinalFields = true;
				} else {
					System.out.println("Not supporting type " + type + " for field " + f.getName());
				}
			}
		}
		
		if (gotStaticFinalFields) {
			buffer.append("\r\n");
		}

		buffer.append("}\r\n");
		return buffer.toString();
	}

	@SuppressWarnings("deprecation")
	public static String generateHeaderFile(SimpleSerializable s) {
		StringBuffer buffer = new StringBuffer();
		Date date = new Date();
		buffer.append("/**\r\n");
		buffer.append(" * Generated by Java2Script.\r\n");
		buffer.append(" * Copyright (c) ");
		buffer.append(date.getYear() + 1900);
		buffer.append(" ");
		buffer.append(company);
		buffer.append(". All rights reserved.\r\n");
		buffer.append(" */\r\n");
		buffer.append("\r\n");

		Class<?> clazz = s.getClass();
		String clazzName = clazz.getName();
		String simpleClazzName = clazzName;
		int idx = clazzName.lastIndexOf('.');
		if (idx != -1) {
			buffer.append("package ");
			buffer.append(simpleClazzName.substring(0, idx));
			buffer.append(";\r\n");
			buffer.append("\r\n");
			
			simpleClazzName = clazzName.substring(idx + 1);
		}
		
		boolean hasMoreImports = false;
		Set<String> importedClasses = new HashSet<String>();

		Type[] interfaces = s.getClass().getGenericInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			for (int i = 0; i < interfaces.length; i++) {
				Class<?> t = (Class<?>) interfaces[i];
				if (!SimpleSerializable.isSubInterfaceOf(t, ISimpleConstant.class)) {
					continue;
				}
				String typeName = t.getName();
				hasMoreImports = true;
				if (!importedClasses.contains(typeName)) {
					String simpleTypeName = typeName;
					buffer.append("import ");
					buffer.append(simpleTypeName);
					buffer.append(";\r\n");
					importedClasses.add(typeName);
				}
			}
		}


		boolean gotStaticFinalFields = false;
		Field[] clazzFields = clazz.getDeclaredFields();
		
		Map<String, Field> fields = new HashMap<String, Field>();
		for (int i = 0; i < clazzFields.length; i++) {
			Field f = clazzFields[i];
			int modifiers = f.getModifiers();
			if ((modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0
					&& (modifiers & (Modifier.TRANSIENT | Modifier.STATIC)) == 0) {
				fields.put(f.getName(), f);
			}
		}

		for (Iterator<Field> itr = fields.values().iterator(); itr.hasNext();) {
			Field field = (Field) itr.next();
			Class<?> type = field.getType();
			
			if (SimpleSerializable.isSubclassOf(type, SimpleSerializable.class)
					|| SimpleSerializable.isSubclassOf(type, SimpleSerializable[].class)) {
				hasMoreImports = true;
				String typeName = type.isArray() ? type.getComponentType().getName() : type.getName();
				if (!importedClasses.contains(typeName)) {
					buffer.append("import ");
					buffer.append(typeName);
					buffer.append(";\r\n");
					importedClasses.add(typeName);
				}
			}
		}

		Class<?> superClazz = s.getClass().getSuperclass();
		if (superClazz != null) {
			String superClazzName = superClazz.getName();
			buffer.append("import ");
			buffer.append(superClazzName);
			buffer.append(";\r\n");
			if (SimpleSerializable.isSubclassOf(superClazz, SimplePipeRunnable.class)) {
				Method[] methods = s.getClass().getMethods();
				if (methods != null) {
					for (int i = 0; i < methods.length; i++) {
						Method m = methods[i];
						if ("deal".equals(m.getName())) {
							Class<?>[] params = m.getParameterTypes();
							if (params != null && params.length == 1) {
								Class<?> paramType = params[0];
								String paramClazzName = paramType.getName();
								buffer.append("import ");
								buffer.append(paramClazzName);
								buffer.append(";\r\n");
							}
						}
					}
				}
			}

			idx = superClazzName.lastIndexOf('.');
			if (idx != -1) {
				superClazzName = superClazzName.substring(idx + 1);
			}
			
			buffer.append("\r\n");
			
			buffer.append("public class ");
			buffer.append(simpleClazzName);
			buffer.append(" extends ");
			buffer.append(superClazzName);
		} else {
			if (hasMoreImports) {
				buffer.append("\r\n");
			}
			buffer.append("public class ");
			buffer.append(simpleClazzName);
		}

		if (interfaces != null && interfaces.length > 0) {
			boolean keywordAppended = false;
			for (int i = 0; i < interfaces.length; i++) {
				Class<?> t = (Class<?>) interfaces[i];
				if (!SimpleSerializable.isSubInterfaceOf(t, ISimpleConstant.class)) {
					continue;
				}
				if (!keywordAppended) {
					buffer.append(" implements ");
					keywordAppended = true;
				}
				String typeName = t.getName();
				String simpleTypeName = typeName;
				idx = simpleTypeName.lastIndexOf('.');
				if (idx != -1) {
					simpleTypeName = simpleTypeName.substring(idx + 1);
				}
				
				buffer.append(simpleTypeName);
				if (i != interfaces.length -1) {
					buffer.append(", ");
				}
			}
		}

		buffer.append(" {\r\n\r\n");
		
		for (int i = 0; i < clazzFields.length; i++) {
			Field f = clazzFields[i];
			int modifiers = f.getModifiers();
			if ((modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0
					&& (modifiers & Modifier.STATIC) != 0 && (modifiers & Modifier.FINAL) != 0) {
				Class<?> type = f.getType();
				if (type == int.class || type == long.class || type == short.class 
						|| type == byte.class || type == char.class || type == double.class
						|| type == float.class || type == boolean.class || type == String.class) {
					buffer.append("\tpublic static final ");
					buffer.append(type.getSimpleName());
					buffer.append(" ");
					buffer.append(f.getName());
					buffer.append(" = ");
					try {
						if (type == int.class) {
							buffer.append("" + f.getInt(s.getClass()));
						} else if (type == long.class) {
							buffer.append(f.getLong(s.getClass()) + "L");
						} else if (type == short.class) {
							buffer.append("" + f.getShort(s.getClass()));
						} else if (type == byte.class) {
							buffer.append("" + f.getByte(s.getClass()));
						} else if (type == char.class) {
							buffer.append("\'" + f.getChar(s.getClass()) + "\'");
						} else if (type == float.class) {
							buffer.append("" + f.getFloat(s.getClass()));
						} else if (type == double.class) {
							buffer.append("" + f.getDouble(s.getClass()));
						} else if (type == boolean.class) {
							if (f.getBoolean(s.getClass())) {
								buffer.append("true");
							} else {
								buffer.append("false");
							}
						} else if (type == String.class) {
							buffer.append("\"" + f.get(s.getClass()) + "\"");
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					buffer.append(";\r\n");
					gotStaticFinalFields = true;
				} else {
					System.out.println("Not supporting type " + type + " for field " + f.getName());
				}
			}
		}
		
		if (gotStaticFinalFields) {
			buffer.append("\r\n");
		}

		for (Iterator<Field> itr = fields.values().iterator(); itr.hasNext();) {
			Field field = (Field) itr.next();
			String name = field.getName();
			Class<?> type = field.getType();
			
			if (type == int.class || type == long.class || type == short.class 
					|| type == byte.class || type == char.class || type == double.class
					|| type == float.class || type == boolean.class || type == String.class
					|| SimpleSerializable.isSubclassOf(type, SimpleSerializable.class)
					|| type == int[].class || type == long[].class || type == double[].class
					|| type == byte[].class || type == short[].class || type == char[].class
					|| type == float[].class || type == boolean[].class || type == String[].class
					|| SimpleSerializable.isSubclassOf(type, SimpleSerializable[].class)) {
				buffer.append("\tpublic ");
				buffer.append(type.getSimpleName());
				buffer.append(" ");
				buffer.append(name);
				buffer.append(";\r\n");
			} else {
				System.out.println("Unsupported type " + type);
			}
		}
		
		buffer.append("\r\n");
		if (s.bytesCompactMode()) {
			buffer.append("\tpublic boolean bytesCompactMode() {\r\n");
			buffer.append("\t\treturn true;\r\n");
			buffer.append("\t}\r\n");
			buffer.append("\r\n");
		}
		if (s instanceof SimplePipeRunnable) {
			buffer.append("\t@Override\r\n");
			buffer.append("\tpublic boolean pipeSetup() {\r\n");
			buffer.append("\t\treturn true;\r\n");
			buffer.append("\t}\r\n");
			buffer.append("\r\n");
			buffer.append("\t@Override\r\n");
			buffer.append("\tpublic SimpleSerializable[] through(Object... args) {\r\n");
			buffer.append("\t\treturn null;\r\n");
			buffer.append("\t}\r\n");
			buffer.append("\r\n");
			
			Method[] methods = s.getClass().getMethods();
			if (methods != null) {
				for (int i = 0; i < methods.length; i++) {
					Method m = methods[i];
					if ("deal".equals(m.getName())) {
						Class<?>[] params = m.getParameterTypes();
						if (params != null && params.length == 1) {
							Class<?> paramType = params[0];
							String paramClazzName = paramType.getName();
							if ("net.sf.j2s.ajax.SimpleSerializable".equals(paramClazzName)) {
								continue;
							}
							int paramIdx = paramClazzName.lastIndexOf('.');
							if (paramIdx != -1) {
								paramClazzName = paramClazzName.substring(paramIdx + 1);
							}
							buffer.append("\tpublic boolean deal(");
							buffer.append(paramClazzName);
							buffer.append(" e) {\r\n");
							buffer.append("\t\treturn true;\r\n");
							buffer.append("\t}\r\n");
							buffer.append("\r\n");
						}
					}
				}
			}
		} else if (s instanceof SimpleRPCRunnable) {
			buffer.append("\t@Override\r\n");
			buffer.append("\tpublic void ajaxRun() {\r\n");
			buffer.append("\t}\r\n");
			buffer.append("\r\n");
		}
		buffer.append("}\r\n");

		return buffer.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("Usage: " + SimpleSource4Java.class.getName() + " <sources folder> <author> <orgization or company> <class> [class ...]");
			return;
		}
		String targetFolder = args[0];
		File f = new File(targetFolder);
		if (f.exists()) {
			if (!f.isDirectory()) {
				System.out.println("Target folder " + f.getAbsolutePath() + " is not a folder.");
				return;
			}
		} else {
			boolean ok = f.mkdirs();
			if (!ok) {
				System.out.println("Failed to create target folder " + f.getAbsolutePath() + ".");
				return;
			}
		}
		folder = f.getName();
		author = args[1];
		company = args[2];
		
		for (int i = 1 + 2; i < args.length; i++) {
			String j2sSimpleClazz = args[i];
			try {
				Class<?> clazz = Class.forName(j2sSimpleClazz);
				if (clazz.isInterface()) {
					String simpleName = j2sSimpleClazz;
					String packageName = null;
					int idx = j2sSimpleClazz.lastIndexOf('.');
					if (idx != -1) {
						packageName = j2sSimpleClazz.substring(0, idx);
						packageName = packageName.replace('.', File.separatorChar);
						simpleName = j2sSimpleClazz.substring(idx + 1);
					}
					String h = generateHeaderFile(clazz);
					String targetPath = targetFolder;
					if (packageName != null) {
						if (targetPath.endsWith(File.separator)) {
							targetPath = targetPath + packageName;
						} else {
							targetPath = targetPath + File.separator + packageName;
						}
						File folder = new File(targetPath);
						if (!folder.exists()) {
							folder.mkdirs();
						}
					}
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(new File(targetPath, simpleName + ".java"));
						fos.write(h.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					System.out.println(new File(targetFolder, simpleName + ".java").getAbsolutePath());
					continue;
				}
				Object inst = clazz.newInstance();
				if (inst instanceof SimpleSerializable) {
					SimpleSerializable s = (SimpleSerializable) inst;
					
					String simpleName = j2sSimpleClazz;
					String packageName = null;
					int idx = j2sSimpleClazz.lastIndexOf('.');
					if (idx != -1) {
						packageName = j2sSimpleClazz.substring(0, idx);
						packageName = packageName.replace('.', File.separatorChar);
						simpleName = j2sSimpleClazz.substring(idx + 1);
					}
					String h = generateHeaderFile(s);
					String targetPath = targetFolder;
					if (packageName != null) {
						if (targetPath.endsWith(File.separator)) {
							targetPath = targetPath + packageName;
						} else {
							targetPath = targetPath + File.separator + packageName;
						}
						File folder = new File(targetPath);
						if (!folder.exists()) {
							folder.mkdirs();
						}
					}
					FileOutputStream fos = null;
					File javaFile = new File(targetPath, simpleName + ".java");
					try {
						fos = new FileOutputStream(javaFile);
						fos.write(h.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					System.out.println(javaFile.getAbsolutePath());
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

}
