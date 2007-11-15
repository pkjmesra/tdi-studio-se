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
package org.talend.designer.core.ui.editor.palette;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.PaletteViewerKeyHandler;

/**
 * 
 */
public class TalendPaletteViewer extends PaletteViewer {

    public TalendPaletteViewer(EditDomain graphicalViewerDomain) {
        setEditDomain(graphicalViewerDomain);
        setKeyHandler(new PaletteViewerKeyHandler(this));
        setEditPartFactory(new TalendPaletteEditPartFactory());
    }
}
