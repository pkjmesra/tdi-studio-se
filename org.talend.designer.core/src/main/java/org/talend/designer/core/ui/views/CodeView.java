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
package org.talend.designer.core.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.commons.exception.SystemException;
import org.talend.commons.ui.swt.colorstyledtext.ColorManager;
import org.talend.commons.ui.swt.colorstyledtext.ColorStyledText;
import org.talend.core.CorePlugin;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.process.IExternalNode;
import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ECodeLanguage;
import org.talend.core.model.temp.ECodePart;
import org.talend.designer.codegen.ICodeGenerator;
import org.talend.designer.codegen.ICodeGeneratorService;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodeLabel;
import org.talend.designer.core.ui.editor.nodes.NodeLabelEditPart;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.outline.NodeTreeEditPart;

/**
 * View that will show the code of the current component.
 * 
 * $Id$
 * 
 */
public class CodeView extends ViewPart implements ISelectionListener {

    private ColorStyledText text;

    private ICodeGenerator codeGenerator = null;

    private boolean mainOnly = true;

    private INode node = null;

    IAction viewMainAction = new Action() {

        @Override
        public String getText() {
            return "Main";
        }

        @Override
        public void run() {
            mainOnly = true;
            refresh();
        }

    };

    IAction viewAllAction = new Action() {

        @Override
        public String getText() {
            return "All";
        }

        @Override
        public void run() {
            mainOnly = false;
            refresh();
        }

    };

    public CodeView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new FillLayout());
        ColorManager colorManager = new ColorManager(CorePlugin.getDefault().getPreferenceStore());
        ECodeLanguage language = ((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                .getProject().getLanguage();
        text = new ColorStyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL, colorManager, language.getName());
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        text.setEditable(false);
        Font font = new Font(parent.getDisplay(), "courier", 8, SWT.NONE);
        text.setFont(font);

        createMenu();
    }

    private void createMenu() {
        IMenuManager manager = getViewSite().getActionBars().getMenuManager();

        manager.add(viewMainAction);
        manager.add(viewAllAction);
        viewMainAction.setChecked(true);
    }

    @Override
    public void setFocus() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object input = ((IStructuredSelection) selection).getFirstElement();
            if (input instanceof NodeTreeEditPart) {
                NodeTreeEditPart nTreePart = (NodeTreeEditPart) input;
                node = (Node) nTreePart.getModel();
                IExternalNode externalNode = ((Node) node).getExternalNode();
                if (externalNode != null) {
                    node = externalNode;
                }
                refresh();
            } else {
                if (input instanceof NodePart) {
                    NodePart editPart = (NodePart) input;
                    node = (Node) editPart.getModel();
                    IExternalNode externalNode = ((Node) node).getExternalNode();
                    if (externalNode != null) {
                        node = externalNode;
                    }
                    refresh();
                } else if (input instanceof NodeLabelEditPart) {
                    NodeLabelEditPart editPart = (NodeLabelEditPart) input;
                    node = (Node) ((NodeLabel) editPart.getModel()).getNode();
                    IExternalNode externalNode = ((Node) node).getExternalNode();
                    if (externalNode != null) {
                        node = externalNode;
                    }
                    refresh();
                }
            }
        }
    }

    public void refresh() {
        if (node != null) {
            String generatedCode;
            if (codeGenerator == null) {
                ICodeGeneratorService service = DesignerPlugin.getDefault().getCodeGeneratorService();
                codeGenerator = service.createCodeGenerator();
            }
            if (mainOnly) {
                viewMainAction.setChecked(true);
                viewAllAction.setChecked(false);
                try {
                    generatedCode = codeGenerator.generateComponentCode(node, ECodePart.MAIN);
                } catch (SystemException e) {
                    throw new RuntimeException(e);
                }
            } else {
                viewMainAction.setChecked(false);
                viewAllAction.setChecked(true);
                try {
                    generatedCode = codeGenerator.generateComponentCode(node, ECodePart.BEGIN);
                    generatedCode += codeGenerator.generateComponentCode(node, ECodePart.MAIN);
                    generatedCode += codeGenerator.generateComponentCode(node, ECodePart.END);
                } catch (SystemException e) {
                    throw new RuntimeException(e);
                }
            }
            text.setText(generatedCode);
        }
    }

}
