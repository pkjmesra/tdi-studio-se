// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.sqlbuilder.dataset.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dataset.DataSet;
import net.sourceforge.sqlexplorer.util.ImageUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableItem;
import org.talend.sqlbuilder.SqlBuilderPlugin;

/**
 * Export table contents to a CSV file.
 * @author Davy Vanherbergen
 */
public class ExportCSVAction extends AbstractDataSetTableContextAction {

    private static final ImageDescriptor IMAGE = ImageUtil.getDescriptor("Images.ExportIcon");


    /**
     * Return the text that will be displayed in the context popup menu for this action. 
     */
    public String getText() {
        return Messages.getString("DataSetTable.Actions.Export.CSV");
    }

    /**
     * Provide image for action
     */
    public ImageDescriptor getImageDescriptor() {
        return IMAGE;
    }

    /**
     * Main method. Prompt for file name and save table contents to csv file.
     */
    public void run() {

        // get filename
        FileDialog fileDialog = new FileDialog(ptable.getShell(), SWT.SAVE);        
        String[] filterExtensions = new String[] {"*.csv"};
        fileDialog.setFilterExtensions(filterExtensions);       
        
        final String fileName = fileDialog.open();
        if (fileName == null && fileName.trim().length() == 0) {
            return;
        }
        
        // let's show the fancy wait cursor..
        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

            public void run() {

                try {

                    // create new file
                    File file = new File(fileName);

                    if (file.exists()) {
                        // overwrite existing files
                        file.delete();
                    }
                    
                    file.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    StringBuffer buffer = new StringBuffer("");
                    
                    // get column header and separator preferences
                    String columnSeparator = SqlBuilderPlugin.getDefault().getPreferenceStore().getString(IConstants.CLIP_EXPORT_SEPARATOR);
                    boolean includeColumnNames = SqlBuilderPlugin.getDefault().getPreferenceStore().getBoolean(IConstants.CLIP_EXPORT_COLUMNS);
                                       
                    // check if there is somethign in our table
                    TableItem[] items = ptable.getItems();                    
                    DataSet dataSet = (DataSet) ptable.getData();
                    
                    if (items == null || dataSet == null) {
                        return;
                    }
                    
                    // export column names if we need to 
                    if (includeColumnNames) {
                        
                        String[] columnNames = dataSet.getColumnLabels();
                        for (int i = 0; i < columnNames.length; i++) {
                            buffer.append(columnNames[i]);
                            buffer.append(columnSeparator);
                        }
                        writer.write(buffer.toString(), 0, buffer.length());
                        writer.newLine();
                    }

                    // export column data
                    int columnCount = ptable.getColumnCount();
                    for (int i = 0; i < items.length; i++) {
                                           
                        buffer = new StringBuffer("");
                        
                        for (int j = 0; j < columnCount; j++) {
                            buffer.append(items[i].getText(j));
                            buffer.append(columnSeparator);
                        }
                        writer.write(buffer.toString(), 0, buffer.length());
                        writer.newLine();
                    }

                    writer.close();


                } catch (final Exception e) {
                    ptable.getShell().getDisplay().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openError(ptable.getShell(), Messages.getString("SQLResultsView.Error.Export.Title"), e.getMessage());
                        }
                    });
                }
            }
        });

    }

}
