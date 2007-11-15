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
package org.talend.designer.mapper.model.tableentry;

import java.util.List;

import org.talend.core.model.process.Problem;
import org.talend.designer.abstractmap.model.table.IDataMapTable;
import org.talend.designer.abstractmap.model.tableentry.ITableEntry;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public abstract class DataMapTableEntry implements ITableEntry {

	private String expression;

	private IDataMapTable parent;

	private String name;

	private List<Problem> problems;

	public DataMapTableEntry(IDataMapTable abstractDataMapTable,
			String expression) {
		super();
		this.parent = abstractDataMapTable;
		this.expression = expression;
	}

	public DataMapTableEntry(IDataMapTable abstractDataMapTable) {
		this(abstractDataMapTable, null);
	}

	/**
	 * DOC amaumont ITableEntry constructor comment.
	 * 
	 * @param abstractDataMapTable
	 * @param name,
	 *            can't be null
	 */
	public DataMapTableEntry(IDataMapTable abstractDataMapTable, String name,
			String expression) {
		super();
		this.parent = abstractDataMapTable;
		this.name = name;
		if (this.name == null) {
			throw new IllegalArgumentException(
					"Name of the TableEntry must not be null !"); //$NON-NLS-1$
		}
		this.expression = expression;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public IDataMapTable getParent() {
		return this.parent;
	}

	public void setParent(IDataMapTable parent) {
		this.parent = parent;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		if (parent != null) {
			return parent.getName();
		} else {
			throw new IllegalStateException("parent null"); //$NON-NLS-1$
		}
	}

	public List<Problem> getProblems() {
		return this.problems;
	}

	public void setProblems(List<Problem> problems) {
		this.problems = problems;
	}

}
