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
package org.talend.repository.ui.wizards.register;

import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.RepositoryConstants;

/**
 * Page for new project details. <br/>
 * 
 * $Id$
 * 
 */
public class RegisterWizardPage extends WizardPage {

    /**
     * 
     */
    private static final String DESCRIPTION = Messages.getString("RegisterWizardPage.description"); //$NON-NLS-1$

    /** EMail field. */
    private Text emailText;

    /** Country. */
    private Combo countryCombo;

    private int countryToSelect = 0;

    private IStatus emailStatus;

    private Label httpProxyHostLabel;

    private Label httpProxyPortLabel;

    private Text httpProxyHostText;

    private Text httpProxyPortText;

    private Button enableHttpProxy;

    /**
     * Constructs a new RegisterWizardPage.
     * 
     * @param server
     * @param password
     * @param author
     */
    public RegisterWizardPage() {
        super("WizardPage"); //$NON-NLS-1$

        setTitle(Messages.getString("RegisterWizardPage.title")); //$NON-NLS-1$
        setDescription(DESCRIPTION);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        // Email
        Label emailLabel = new Label(container, SWT.NONE);
        emailLabel.setText(Messages.getString("RegisterWizardPage.email")); //$NON-NLS-1$

        emailText = new Text(container, SWT.BORDER);
        emailText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Country
        Label countryLabel = new Label(container, SWT.NONE);
        countryLabel.setText(Messages.getString("RegisterWizardPage.country")); //$NON-NLS-1$

        countryCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        countryCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        countryCombo.setItems(initiateCountryList());
        countryCombo.select(countryToSelect);

        createSpacer(container, 2);
        createHttpProxy(container, 2);

        setControl(container);
        addListeners();
        // checkFieldsValue();
        // updatePageStatus();
        setPageComplete(false);
    }

    /**
     * DOC mhirt Comment method "initiateCountryList".
     * 
     * @return
     */
    private String[] initiateCountryList() {
        SortedSet<String> countryList = new TreeSet<String>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayCountry().compareTo("") != 0) {
                countryList.add(locale.getDisplayCountry());
            }
        }
        // initiate selection of default country
        String defaultCountry = Locale.getDefault().getDisplayCountry();
        int i = 0;
        for (String country : countryList) {
            if (country.equals(defaultCountry)) {
                countryToSelect = i;
                break;
            }
            i++;
        }
        return countryList.toArray(new String[] {});
    }

    private void addListeners() {
        emailText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFieldsValue();
            }
        });
    }

    /**
     * DOC ocarbone Comment method "checkField".
     */
    protected void checkFieldsValue() {
        emailStatus = createOkStatus();
        // Email Name
        if (emailText.getText().length() == 0) {
            emailStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID, IStatus.OK, Messages
                    .getString("RegisterWizardPage.emailEmpty"), null); //$NON-NLS-1$
        } else {
            // Reg Exp validation
            if (!Pattern.matches(RepositoryConstants.MAIL_PATTERN, emailText.getText())) {
                emailStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID, IStatus.OK, Messages
                        .getString("RegisterWizardPage.emailNotValid"), null); //$NON-NLS-1$
            }
        }
        updatePageStatus();
    }

    private void updatePageStatus() {
        IStatus findMostSevere = findMostSevere();
        setMessage(findMostSevere);
        setPageComplete((findMostSevere != null) ? (findMostSevere.getSeverity() != IStatus.ERROR) : true);
    }

    private IStatus findMostSevere() {
        IStatus status = null;
        if (emailStatus.getSeverity() == IStatus.ERROR) {
            status = emailStatus;
        }
        return status;
    }

    private void setMessage(IStatus status) {
        if ((status != null) && (IStatus.ERROR == status.getSeverity())) {
            setErrorMessage(status.getMessage());
            setMessage(""); //$NON-NLS-1$
        } else {
            setMessage(DESCRIPTION);
            setErrorMessage(null);
        }
    }

    public String getEmail() {
        return emailText.getText();
    }

    public String getCountry() {
        if (countryCombo.getSelectionIndex() != -1) {
            String selectedCountry = countryCombo.getItem(countryCombo.getSelectionIndex());
            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.getDisplayCountry().compareTo(selectedCountry) == 0) {
                    return locale.getCountry();
                }
            }
        }
        return null;
    }

    private static IStatus createOkStatus() {
        return new Status(IStatus.OK, RepositoryPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
    }

    protected void createHttpProxy(Composite composite, int columnSpan) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.getString("RegisterWizardPage.proxyGroup"));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = columnSpan;
        group.setLayoutData(gd);

        enableHttpProxy = new Button(group, SWT.CHECK);
        enableHttpProxy.setText(Messages.getString("RegisterWizardPage.enableHttpProxy"));
        gd = new GridData();
        gd.horizontalSpan = 2;
        enableHttpProxy.setLayoutData(gd);

        httpProxyHostLabel = new Label(group, SWT.NONE);
        httpProxyHostLabel.setText(Messages.getString("RegisterWizardPage.httpProxyHost"));

        httpProxyHostText = new Text(group, SWT.SINGLE | SWT.BORDER);
        httpProxyHostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        httpProxyPortLabel = new Label(group, SWT.NONE);
        httpProxyPortLabel.setText(Messages.getString("RegisterWizardPage.httpProxyPort"));

        httpProxyPortText = new Text(group, SWT.SINGLE | SWT.BORDER);
        httpProxyPortText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // Validation of port field
        httpProxyPortText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                try {
                    String portValue = httpProxyPortText.getText();
                    int num = 80;
                    if (portValue != null && portValue.trim().length() > 0) {
                        num = Integer.valueOf(portValue).intValue();
                    }
                    if (0 <= num && num <= 0xFFFF) {
                        // port is valid
                        updatePageStatus();
                        setErrorMessage(null);
                        return;
                    }

                    // port is invalid
                } catch (NumberFormatException nfe) {
                }
                setPageComplete(false);
                setErrorMessage(Messages.getString("RegisterWizardPage.invalidPort"));
            }
        });

        enableHttpProxy.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                updateProxyButtons();
            }

        });
        updateProxyButtons();
    }

    /**
     * DOC smallet Comment method "updateProxyButtons".
     */
    private void updateProxyButtons() {
        boolean enable = enableHttpProxy.getSelection();
        httpProxyPortLabel.setEnabled(enable);
        httpProxyHostLabel.setEnabled(enable);
        httpProxyPortText.setEnabled(enable);
        httpProxyHostText.setEnabled(enable);
    }

    protected void createSpacer(Composite composite, int columnSpan) {
        Label label = new Label(composite, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = columnSpan;
        label.setLayoutData(gd);
    }

    /**
     * @return the enableHttpProxy
     */
    public Button getEnableHttpProxy() {
        return enableHttpProxy;
    }

    /**
     * @param enableHttpProxy the enableHttpProxy to set
     */
    public void setEnableHttpProxy(Button enableHttpProxy) {
        this.enableHttpProxy = enableHttpProxy;
    }

    /**
     * @return the httpProxyHostText
     */
    public Text getHttpProxyHostText() {
        return httpProxyHostText;
    }

    /**
     * @param httpProxyHostText the httpProxyHostText to set
     */
    public void setHttpProxyHostText(Text httpProxyHostText) {
        this.httpProxyHostText = httpProxyHostText;
    }

    /**
     * @return the httpProxyPortText
     */
    public Text getHttpProxyPortText() {
        return httpProxyPortText;
    }

    /**
     * @param httpProxyPortText the httpProxyPortText to set
     */
    public void setHttpProxyPortText(Text httpProxyPortText) {
        this.httpProxyPortText = httpProxyPortText;
    }

}
