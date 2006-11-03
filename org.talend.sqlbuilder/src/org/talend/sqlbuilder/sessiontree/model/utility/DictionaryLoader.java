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
package org.talend.sqlbuilder.sessiontree.model.utility;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.talend.sqlbuilder.Messages;
import org.talend.sqlbuilder.sessiontree.model.SessionTreeNode;


/**
 * DOC qianbing  class global comment. Detailled comment
 * <br/>
 *
 * $Id: DictionaryLoader.java,v 1.3 2006/11/01 07:49:10 peiqin.hou Exp $
 *
 */
public class DictionaryLoader extends Job {

    private static final String ID = "net.sourceforge.sqlexplorer";
    
    /**
     * Hidden constructor.
     */
    private DictionaryLoader() {
        super(null);
    }
    
    /**
     * Default constructor.
     * @param sessionNode SessionTreeNode
     */
    public DictionaryLoader(SessionTreeNode sessionNode) {
        super(Messages.getString("Progress.Dictionary.Title"));
    }
    
    /**
     * Load dictionary in background process.
     * @param monitor IProgressMonitor
     * @return IStatus
     */
    protected IStatus run(IProgressMonitor monitor) {
        
        // check if we can persisted dictionary 
        monitor.setTaskName(Messages.getString("Progress.Dictionary.Scanning"));

        return new Status(IStatus.OK, ID, IStatus.OK, "tested ok ", null);
    }

}
