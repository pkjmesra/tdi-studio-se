// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//   
// ============================================================================
package org.talend.designer.rowgenerator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.designer.rowgenerator.RowGeneratorComponent;
import org.talend.designer.rowgenerator.managers.UIManager;
import org.talend.designer.rowgenerator.ui.editor.MetadataColumnExt;
import org.talend.expressionbuilder.test.shadow.Variable;

/**
 * class global comment. Detailled comment <br/>
 * 
 * $Id: FunctionManager.java,v 1.13 2007/01/31 05:20:51 pub Exp $
 * 
 */
public class FunctionManagerExt extends FunctionManager {

	private static boolean addPreSuffix = true;

	public FunctionManagerExt() {
		super();
	}

	public Function getCurrentFunction(String funName, MetadataColumnExt bean) {
		Function currentFun = new Function();
		List<Function> functions = getFunctionByName(bean.getTalendType());
		String[] arrayTalendFunctions2 = new String[functions.size()];
		if (functions.isEmpty()) {
			currentFun.setDescription(""); //$NON-NLS-1$
			currentFun.setPreview(""); //$NON-NLS-1$
			currentFun.setParameters(new ArrayList<Parameter>());
			bean.setArrayFunctions(arrayTalendFunctions2);
		} else {
			for (int i = 0; i < functions.size(); i++) {
				arrayTalendFunctions2[i] = functions.get(i).getName();
				if (funName.equals(functions.get(i).getName())) {
					currentFun = functions.get(i);
				}
			}
			bean.setArrayFunctions(arrayTalendFunctions2);
		}
		return currentFun;
	}

	public Function getDefaultFunction(MetadataColumnExt bean, String talendType) {
		Function currentFun = new Function();
		List<Function> functions = getFunctionByName(talendType);
		String[] arrayTalendFunctions2 = new String[functions.size()];
		if (functions.isEmpty()) {
			currentFun.setDescription(""); //$NON-NLS-1$
			currentFun.setPreview(""); //$NON-NLS-1$
			currentFun.setParameters(new ArrayList<Parameter>());
			bean.setArrayFunctions(arrayTalendFunctions2);
		} else {
			for (int i = 0; i < functions.size(); i++) {
				arrayTalendFunctions2[i] = functions.get(i).getName();
			}
			currentFun = (Function) functions.get(0).clone();
			bean.setArrayFunctions(arrayTalendFunctions2);
		}

		return currentFun;
	}

	public Function getFuntionFromArray(MetadataColumnExt bean,
			RowGeneratorComponent externalNode, int index) {
		String value = externalNode.getColumnValue(bean, index);
		List<Function> functions = getFunctionByName(bean.getTalendType());
		Function currentFun = getAvailableFunFromValue(value, functions);

		if (currentFun == null) {
			currentFun = new Function();
			String[] arrayTalendFunctions2 = new String[functions.size()];
			if (functions.isEmpty()) {
				currentFun.setDescription(""); //$NON-NLS-1$
				currentFun.setPreview(""); //$NON-NLS-1$
				currentFun.setParameters(new ArrayList<Parameter>());
				bean.setArrayFunctions(arrayTalendFunctions2);
			} else {
				for (int i = 0; i < functions.size(); i++) {
					arrayTalendFunctions2[i] = functions.get(i).getName();
				}
				currentFun = (Function) functions.get(0).clone();
				bean.setArrayFunctions(arrayTalendFunctions2);
			}
		}

		return currentFun;

	}

	/**
	 * qzhang Comment method "isAvailableSubValue".
	 * 
	 * @param value
	 * @return
	 */
	private Function getAvailableFunFromValue(String value, List<Function> funs) {
		Function currentFun = null;
		boolean isExsit = false;
		for (int i = 0; i < funs.size() && !isExsit; i++) {
			Function function = funs.get(i);
			if (value != null && value.indexOf(function.getName()) != -1) {
				isExsit = true;
			}
		}
		if (value != null) {
			boolean isPure = true;
			int paramLength = value.length() - 2;
			if (UIManager.isJavaProject()) {
				isPure = value.indexOf(".") != -1
						&& value.indexOf("(") > value.indexOf(".")
						&& value.endsWith(")");
				paramLength = value.length() - 1;
			} else {
				isPure = value.startsWith(PERL_FUN_PREFIX)
						&& value.endsWith(PERL_FUN_SUFFIX);
			}
			if (isPure && isExsit) {
				for (Function function : funs) {
					int indexOf = value.indexOf(function.getName());
					if (indexOf != -1) {
						String para = value.substring(indexOf
								+ function.getName().length() + 1, paramLength);
						if ("".equals(para)) {
							if (function.getParameters().size() == 0) {
								currentFun = (Function) function.clone();
							}
						} else {
							String[] ps = para.split(FUN_PARAM_SEPARATED); //$NON-NLS-1$
							if (ps.length == function.getParameters().size()) {
								currentFun = function.clone(ps);
							}
						}
					}
				}
			} else {
				currentFun = createPureFunctions(value, funs, currentFun);
			}

		}
		return currentFun;
	}

	/**
	 * qzhang Comment method "createPureFunctions".
	 * 
	 * @param value
	 * @param funs
	 * @param currentFun
	 * @return
	 */
	private Function createPureFunctions(String value, List<Function> funs,
			Function currentFun) {
		for (Function function : funs) {
			if (function.getName().equals(PURE_PERL_NAME)) {
				currentFun = (Function) function.clone();
				((StringParameter) currentFun.getParameters().get(0))
						.setValue(value);
			}
		}
		return currentFun;
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	public static String getOneColData(MetadataColumnExt bean) {
		if (bean != null && bean.getFunction() != null) {
			String newValue = addPreSuffix ? PERL_FUN_PREFIX : "";
			if (bean.getFunction().getName().equals(PURE_PERL_NAME)) {
				newValue = ((StringParameter) bean.getFunction()
						.getParameters().get(0)).getValue();
			} else {
				if (bean.getFunction().getName() == null
						|| "".equals(bean.getFunction().getName())) { //$NON-NLS-1$
					return ""; //$NON-NLS-1$
				}
				final List<Parameter> parameters = bean.getFunction()
						.getParameters();
				if (UIManager.isJavaProject()) {
					String fullName = JavaFunctionParser.getTypeMethods().get(
							bean.getTalendType() + "."
									+ bean.getFunction().getName());
					newValue = fullName + "(";
					for (Parameter pa : parameters) {
						newValue += pa.getValue() + FUN_PARAM_SEPARATED; //$NON-NLS-1$
					}
					if (!parameters.isEmpty()) {
						newValue = newValue.substring(0, newValue.length() - 1);
					}
					newValue += ")"; //$NON-NLS-1$

				} else {
					newValue += bean.getFunction().getName() + "("; //$NON-NLS-1$
					for (Parameter pa : parameters) {
						newValue += pa.getValue() + FUN_PARAM_SEPARATED; //$NON-NLS-1$
					}
					newValue = newValue.substring(0, newValue.length() - 1);

					newValue += addPreSuffix ? PERL_FUN_SUFFIX : ")"; //$NON-NLS-1$

				}
			}
			return newValue;
		}
		return null;
	}

	/**
	 * yzhang Comment method "getOneColData".
	 * 
	 * @param bean
	 * @param f
	 * @return
	 */
	public static String getOneColData(MetadataColumnExt bean, boolean f) {
		addPreSuffix = f;
		String str = getOneColData(bean);
		addPreSuffix = true;
		return str;
	}

}
