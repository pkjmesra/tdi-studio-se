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
package org.talend.designer.rowgenerator.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.designer.rowgenerator.RowGeneratorComponent;
import org.talend.designer.rowgenerator.data.FunctionManagerExt;
import org.talend.designer.rowgenerator.external.data.ExternalRowGeneratorUiProperties;
import org.talend.designer.rowgenerator.ui.RowGeneratorUI;
import org.talend.designer.rowgenerator.ui.editor.MetadataColumnExt;
import org.talend.designer.rowgenerator.ui.editor.MetadataToolbarEditorViewExt;

/**
 * qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: UIManager.java,v 1.8 2007/01/31 10:31:05 pub Exp $
 * 
 */
public class UIManager {

	private RowGeneratorUI generatorUI;

	private int rowGenResponse = SWT.NONE;

	private final RowGeneratorManager rgManager;

	private boolean shiftPressed;

	private boolean ctrlPressed;

	private ExternalRowGeneratorUiProperties uiProperties;

	int currentDragDetail;

	/**
	 * amaumont UIManager constructor comment.
	 * 
	 * @param tableManager
	 * @param manager
	 */
	public UIManager(RowGeneratorManager rgManager) {
		this.rgManager = rgManager;
	}

	public ExternalRowGeneratorUiProperties getUiProperties() {
		if (this.uiProperties == null) {
			this.uiProperties = new ExternalRowGeneratorUiProperties();
		}
		return this.uiProperties;
	}

	public void setUiProperties(ExternalRowGeneratorUiProperties uiProperties) {
		this.uiProperties = uiProperties;
	}

	public RowGeneratorUI getGeneratorUI() {
		return this.generatorUI;
	}

	public void setGeneratorUI(RowGeneratorUI generatorUI) {
		this.generatorUI = generatorUI;
	}

	public int getRowGenResponse() {
		return this.rowGenResponse;
	}

	public void setRowGenResponse(int rowGenResponse) {
		this.rowGenResponse = rowGenResponse;
	}

	public boolean isCtrlPressed() {
		return this.ctrlPressed;
	}

	public void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	public boolean isShiftPressed() {
		return this.shiftPressed;
	}

	public void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	/**
	 * qzhang Comment method "closeMapper".
	 * 
	 * @param response
	 */
	public void closeRowGenerator(int response) {
		setRowGenResponse(response);
		Composite parent = generatorUI.getRowGenUIParent();
		saveCurrentUIProperties();
		if (response == SWT.CANCEL) {
			reductAllData();
		}
		if (response == SWT.OK) {
			saveAllData();
		}
		if (parent instanceof Shell) {
			((Shell) parent).close();
		}
	}

	/**
	 * qzhang Comment method "saveAllData".
	 */
	private void saveAllData() {
		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		MetadataTable table = (MetadataTable) rgManager
				.getRowGeneratorComponent().getMetadataList().get(0);
		for (IMetadataColumn col : table.getListColumns()) {
			MetadataColumnExt ext = (MetadataColumnExt) col;
			Map<String, Object> value = new HashMap<String, Object>();
			value.put(RowGeneratorComponent.COLUMN_NAME, ext.getLabel());
			value.put(RowGeneratorComponent.ARRAY, FunctionManagerExt
					.getOneColData(ext));
			map.add(value);
		}
		rgManager.getRowGeneratorComponent().setTableElementParameter(map);
		rgManager.getRowGeneratorComponent().setNumber(
				generatorUI.getDataTableView().getExtendedToolbar()
						.getNumRows());

	}

	/**
	 * qzhang Comment method "saveAllData".
	 */
	private void reductAllData() {
		List<Map<String, Object>> eps = this.getRowGenManager().getOrginEP();
		this.getRowGenManager().getRowGeneratorComponent()
				.setTableElementParameter(eps);
		this.getRowGenManager().getRowGeneratorComponent().setNumber(
				this.getRowGenManager().getOrginNumber());
	}

	/**
	 * qzhang Comment method "saveCurrentUIProperties".
	 */
	private void saveCurrentUIProperties() {
		ExternalRowGeneratorUiProperties.setWeightsMainSashForm(generatorUI
				.getMainSashForm().getWeights());
		ExternalRowGeneratorUiProperties
				.setWeightsDatasFlowViewSashForm(generatorUI
						.getDatasFlowViewSashForm().getWeights());
		ExternalRowGeneratorUiProperties.setShellMaximized(generatorUI
				.getRowGenUIParent().getShell().getMaximized());
		if (!ExternalRowGeneratorUiProperties.isShellMaximized()) {
			ExternalRowGeneratorUiProperties.setBoundsRowGen(generatorUI
					.getRowGenUIParent().getBounds());
		}
		ExternalRowGeneratorUiProperties
				.setShowColumnsList(getShowColumnsList());
	}

	/**
	 * qzhang Comment method "getShowColumnsList".
	 * 
	 * @return
	 */
	private String[] getShowColumnsList() {
		List<String> cols = new ArrayList<String>();
		MetadataToolbarEditorViewExt editorViewExt = generatorUI
				.getDataTableView().getExtendedToolbar();
		MenuItem[] items = editorViewExt.getColumnsListmenu().getItems();
		for (MenuItem item : items) {
			if (!item.getSelection()) {
				cols.add(item.getData().toString());
			}
		}
		return cols.toArray(new String[cols.size()]);
	}

	public RowGeneratorManager getRowGenManager() {
		return this.rgManager;
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected void saveOneColData(MetadataColumnExt bean) {
		String newValue2 = FunctionManagerExt.getOneColData(bean);
		if (rgManager.getRowGeneratorComponent() != null && newValue2 != null) {
			rgManager.getRowGeneratorComponent().setColumnValue(
					bean.getLabel(), newValue2);
		}
	}

	public static boolean isJavaProject() {
		return FunctionManagerExt.isJavaProject();
	}
}
