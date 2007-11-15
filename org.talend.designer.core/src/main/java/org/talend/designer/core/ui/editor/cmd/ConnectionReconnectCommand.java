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
package org.talend.designer.core.ui.editor.cmd;

import org.eclipse.gef.commands.Command;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTool;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INodeConnector;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.model.process.ConnectionManager;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * Command that allows to change a connection to a new source or a new target. <br/>
 * 
 * $Id$
 * 
 */
public class ConnectionReconnectCommand extends Command {

    private Connection connection;

    private Node newSource;

    private Node newTarget;

    private Node oldSource;

    private Node oldTarget;

    private String connectorName;

    private EConnectionType newLineStyle;

    private IMetadataTable oldMetadataTable;

    private String oldSourceSchemaType, newSourceSchemaType;

    private String newTargetSchemaType;

    private EConnectionType oldLineStyle;

    /**
     * Initialisation of the command with the given connection. This will initialize the source and target before change
     * them.
     * 
     * @param connection
     */
    public ConnectionReconnectCommand(Connection connection) {
        this.connection = connection;
        this.oldSource = connection.getSource();
        this.oldTarget = connection.getTarget();
        connectorName = connection.getConnectorName();
        oldLineStyle = connection.getLineStyle();
        newLineStyle = oldLineStyle;
        if (oldLineStyle.hasConnectionCategory(IConnectionCategory.DATA)) {
            oldMetadataTable = connection.getMetadataTable().clone();
        }
        oldSourceSchemaType = (String) oldSource.getPropertyValue(EParameterName.SCHEMA_TYPE.getName());
    }

    /**
     * Set a new source for the connection.
     * 
     * @param nodeSource
     */
    public void setNewSource(Node nodeSource) {
        setLabel(Messages.getString("ConnectionReconnectCommand.LabelSource")); //$NON-NLS-1$
        newSource = nodeSource;
        newTarget = null;
    }

    /**
     * Set a new target for the connection.
     * 
     * @param connectionTarget
     */
    public void setNewTarget(Node nodeTarget) {
        setLabel(Messages.getString("ConnectionReconnectCommand.LabelTarget")); //$NON-NLS-1$
        newSource = null;
        newTarget = nodeTarget;
    }

    public boolean canExecute() {
        boolean canExecute = false;
        if (!connection.isActivate()) {
            return false;
        }
        if (newSource != null) {
            // remove the connection for the check
            connection.setPropertyValue(EParameterName.ACTIVATE.getName(), false);
            canExecute = checkSourceReconnection(); // check
            connection.setPropertyValue(EParameterName.ACTIVATE.getName(), true);
        } else if (newTarget != null) {
            connection.setPropertyValue(EParameterName.ACTIVATE.getName(), false);
            canExecute = checkTargetReconnection();
            connection.setPropertyValue(EParameterName.ACTIVATE.getName(), true);
        }
        return canExecute;
    }

    /**
     * Check if a new connection on the selected source is allowed.
     * 
     * @return true / false
     */
    private boolean checkSourceReconnection() {
        if (!ConnectionManager.canConnectToSource(oldSource, newSource, oldTarget, oldLineStyle, connectorName, connection
                .getName())) {
            return false;
        }
        newLineStyle = ConnectionManager.getNewConnectionType();

        return true;
    }

    /**
     * Check if a new connection on the selected target is allowed.
     * 
     * @return true / false
     */
    private boolean checkTargetReconnection() {
        if (!ConnectionManager.canConnectToTarget(oldSource, oldTarget, newTarget, oldLineStyle, connectorName, connection
                .getName())) {
            return false;
        }
        newLineStyle = ConnectionManager.getNewConnectionType();

        return true;
    }

    private void setSchemaToNotBuiltInNode(Node oldNode, Node newNode, IMetadataTable newSchema) {
        if ((newNode.getMetadataList() != null) && newNode.getMetadataList().get(0).getListColumns().size() == 0) {
            // only override if there is no schema defined in the component
            String sourceConnector = oldMetadataTable.getAttachedConnector();
            String baseConnector = oldNode.getConnectorFromName(sourceConnector).getBaseSchema();
            for (INodeConnector connector : newNode.getListConnector()) {
                if (connector.getBaseSchema().equals(baseConnector)) {
                    IMetadataTable meta = newNode.getMetadataFromConnector(connector.getName());
                    meta.setDescription(newSchema.getDescription());
                    MetadataTool.copyTable(newSchema, meta);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    public void execute() {
        if (newSource != null) {
            INodeConnector connector = oldSource.getConnectorFromName(connectorName);
            connector.setCurLinkNbOutput(connector.getCurLinkNbOutput() - 1);
            connector = newSource.getConnectorFromName(connectorName);
            connector.setCurLinkNbOutput(connector.getCurLinkNbOutput() + 1);
            if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.FLOW)) {
                newSourceSchemaType = (String) newSource.getPropertyValue(EParameterName.SCHEMA_TYPE.getName());
                boolean builtInNewSource = newSource.getConnectorFromName(connectorName).isBuiltIn();
                boolean builtInOldSource = oldSource.getConnectorFromName(connectorName).isBuiltIn();
                if ((!builtInNewSource) && (!builtInOldSource)) {
                    setSchemaToNotBuiltInNode(oldSource, newSource, oldMetadataTable);
                    connection.setMetaName(newSource.getUniqueName());
                } else {
                    if (!builtInNewSource) {
                        int num = 0;
                        for (int i = 0; i < oldSource.getMetadataList().size(); i++) {
                            IMetadataTable meta = oldSource.getMetadataList().get(i);
                            if (meta.getTableName().equals(oldMetadataTable.getTableName())) {
                                num = i;
                            }
                        }
                        oldSource.getMetadataList().remove(num);
                        setSchemaToNotBuiltInNode(oldSource, newSource, oldMetadataTable);
                        connection.setMetaName(newSource.getUniqueName());
                    }
                    if (!builtInOldSource) {
                        IMetadataTable meta = oldMetadataTable.clone();
                        meta.setTableName(connection.getUniqueName());
                        newSource.getMetadataList().add(meta);
                        connection.setMetaName(meta.getTableName());
                    }
                    if ((builtInOldSource) && (builtInNewSource)) {
                        int num = 0;
                        for (int i = 0; i < oldSource.getMetadataList().size(); i++) {
                            IMetadataTable meta = oldSource.getMetadataList().get(i);
                            if (meta.getTableName().equals(oldMetadataTable.getTableName())) {
                                num = i;
                            }
                        }
                        oldSource.getMetadataList().remove(num);
                        newSource.getMetadataList().add(oldMetadataTable);
                    }
                }
                if (newSourceSchemaType != null) {
                    newSource.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), EmfComponent.BUILTIN);
                }
                if (oldSourceSchemaType != null) {
                    oldSource.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), EmfComponent.BUILTIN);
                }
            } else {
                connection.setMetaName(newSource.getUniqueName());
            }
            connection.reconnect(newSource, oldTarget, newLineStyle);
            connection.updateName();
            ((Process) newSource.getProcess()).checkStartNodes();
            ((Process) newSource.getProcess()).checkProcess();
        } else if (newTarget != null) {
            newTargetSchemaType = (String) newTarget.getPropertyValue(EParameterName.SCHEMA_TYPE.getName());
            INodeConnector connector = oldTarget.getConnectorFromType(oldLineStyle);
            connector.setCurLinkNbInput(connector.getCurLinkNbInput() - 1);
            connector = newTarget.getConnectorFromType(newLineStyle);
            connector.setCurLinkNbInput(connector.getCurLinkNbInput() + 1);
            connection.reconnect(oldSource, newTarget, newLineStyle);
            connection.updateName();
            if (newTargetSchemaType != null) {
                newTarget.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), EmfComponent.BUILTIN);
            }
            ((Process) oldSource.getProcess()).checkStartNodes();
            ((Process) oldSource.getProcess()).checkProcess();
        } else {
            throw new IllegalStateException("Should not happen"); //$NON-NLS-1$
        }
    }

    public void undo() {
        if (newSource != null) {
            INodeConnector connector = oldSource.getConnectorFromName(connectorName);
            connector.setCurLinkNbOutput(connector.getCurLinkNbOutput() + 1);
            connector = newSource.getConnectorFromName(connectorName);
            connector.setCurLinkNbOutput(connector.getCurLinkNbOutput() - 1);
            if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.FLOW)) {
                boolean builtInNewSource = newSource.getConnectorFromName(connectorName).isBuiltIn();
                boolean builtInOldSource = oldSource.getConnectorFromName(connectorName).isBuiltIn();
                if ((!builtInNewSource) && (!builtInOldSource)) {
                    setSchemaToNotBuiltInNode(newSource, oldSource, oldMetadataTable);
                    connection.setMetaName(oldSource.getUniqueName());
                } else {
                    if (!builtInNewSource) {
                        oldSource.getMetadataList().add(oldMetadataTable);
                        connection.setMetaName(oldMetadataTable.getTableName());
                    }
                    if (!builtInOldSource) {
                        int num = 0;
                        for (int i = 0; i < newSource.getMetadataList().size(); i++) {
                            IMetadataTable meta = newSource.getMetadataList().get(i);
                            if (meta.getTableName().equals(connection.getUniqueName())) {
                                num = i;
                            }
                        }
                        newSource.getMetadataList().remove(num);
                        setSchemaToNotBuiltInNode(newSource, oldSource, oldMetadataTable);
                        connection.setMetaName(oldSource.getUniqueName());
                    }
                    if ((builtInOldSource) && (builtInNewSource)) {
                        int num = 0;
                        for (int i = 0; i < newSource.getMetadataList().size(); i++) {
                            IMetadataTable meta = newSource.getMetadataList().get(i);
                            if (meta.getTableName().equals(oldMetadataTable.getTableName())) {
                                num = i;
                            }
                        }
                        newSource.getMetadataList().remove(num);
                        oldSource.getMetadataList().add(oldMetadataTable);
                    }
                }
                if (newSourceSchemaType != null) {
                    newSource.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), newSourceSchemaType);
                }
                if (oldSourceSchemaType != null) {
                    oldSource.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), oldSourceSchemaType);
                }
            } else {
                connection.setMetaName(oldSource.getUniqueName());
            }
        } else if (newTarget != null) {
            INodeConnector connector = oldTarget.getConnectorFromType(oldLineStyle);
            connector.setCurLinkNbInput(connector.getCurLinkNbInput() + 1);
            connector = newTarget.getConnectorFromType(newLineStyle);
            connector.setCurLinkNbInput(connector.getCurLinkNbInput() - 1);
            if (newTargetSchemaType != null) {
                newTarget.setPropertyValue(EParameterName.SCHEMA_TYPE.getName(), newTargetSchemaType);
            }
        }
        connection.reconnect(oldSource, oldTarget, oldLineStyle);
        connection.updateName();
        ((Process) oldSource.getProcess()).checkStartNodes();
        ((Process) oldSource.getProcess()).checkProcess();
    }

}
