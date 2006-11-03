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
package org.talend.sqlbuilder.sessiontree.model;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.talend.repository.model.RepositoryNode;

/**
 * Session Tree Model.
 * <br/>
 *
 * $Id: SessionTreeModel.java,v 1.4 2006/11/01 06:45:49 peiqin.hou Exp $
 *
 */
public class SessionTreeModel implements ISessionTreeNode {

    RootSessionTreeNode root = new RootSessionTreeNode();

    private ListenerList listeners = new ListenerList();

    public SessionTreeModel() {

    }

    /**
     * @return RootSessionTreeNode
     */
    public RootSessionTreeNode getRoot() {

        return root;
    }

    /**
     * @return Object[]
     */
    public Object[] getChildren() {
        return new Object[] { root };
    }

    /**
     * @return Parent.
     */
    public Object getParent() {
        return root.getParent();
    }

    /**
     * @param conn Connection
     * @param alias SQLAlias.
     * @param monitor IProgressMonitor
     * @param pswd String 
     * @param repositoryNode RepositoryNode
     * @return SessionTreeNode SessionTreeNode
     * @exception InterruptedException .
     */
    public SessionTreeNode createSessionTreeNode(SQLConnection[] conn, ISQLAlias alias, IProgressMonitor monitor,
            String pswd, RepositoryNode repositoryNode) throws InterruptedException {

        SessionTreeNode node = null;
        try {
            node = new SessionTreeNode(conn, alias, this, monitor, pswd, repositoryNode);
        } finally {
            modelChanged(node);
        }
        return node;
    }

    /**
     * @param listener SessionTreeModelChangedListener
     */
    public void addListener(SessionTreeModelChangedListener listener) {
        listeners.add(listener);
    }
    
    /**
     * @param listener SessionTreeModelChangedListener
     */
    public void removeListener(SessionTreeModelChangedListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * @param stn SessionTreeNode
     */
    public void modelChanged(SessionTreeNode stn) {

        Object[] ls = listeners.getListeners();

        for (int i = 0; i < ls.length; ++i) {
            try {
                ((SessionTreeModelChangedListener) ls[i]).modelChanged(stn);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }
}
