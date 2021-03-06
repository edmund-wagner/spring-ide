/*******************************************************************************
 * Copyright (c) 2006 - 2011 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.ui.wizards;

import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;
import org.springframework.ide.eclipse.beans.ui.BeansUIPlugin;
import org.springframework.ide.eclipse.core.StringUtils;
import org.springframework.ide.eclipse.ui.SpringUIUtils;

/**
 * @author Torsten Juergeleit
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
@SuppressWarnings({ "deprecation", "restriction" })
public class NewSpringProjectCreationPage extends WizardPage {

	private static final String CONFIG_PROPERTY_PREFIX = "ConfigurationPropertyPage."
			+ "tabConfigFiles.";

	private static final String ENABLE_IMPORT_LABEL = CONFIG_PROPERTY_PREFIX
			+ "enableImports.label";

	private static final String IGNORE_MISSING_NAMESPACEHANDLER_LABEL = CONFIG_PROPERTY_PREFIX
			+ "ignoreMissingNamespaceHandler.label";

	private Button classpathCheckbox;
	
	private Button disableNamespaceCachingCheckbox;

	private Button enableImportButton;

	private Button enableProjectFacetsButton;

	private Button ignoreMissingNamespaceHandlerButton;

	private Text suffixesText;

	private SelectionButtonDialogField useProjectSettingsButton;

	private Button versionCheckbox;

	public NewSpringProjectCreationPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(), true));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createConfigFileGroup(composite);
		createNamespaceSettingsGroup(composite);
		createFacetsGroup(composite);
		setPageComplete(validatePage());

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	public boolean enableImports() {
		return enableImportButton.getSelection();
	}

	public boolean enableProjectFacets() {
		return enableProjectFacetsButton.getSelection();
	}

	public Set<String> getConfigSuffixes() {
		return StringUtils.commaDelimitedListToSet(suffixesText.getText());
	}

	public boolean ignoreMissingNamespaceHandlers() {
		return ignoreMissingNamespaceHandlerButton.getSelection();
	}

	public boolean loadHandlerFromClasspath() {
		return classpathCheckbox.getSelection();
	}
	
	public boolean disableNamespaceCaching() {
		return disableNamespaceCachingCheckbox.getSelection();
	}

	public boolean useHighestXsdVersion() {
		return versionCheckbox.getSelection();
	}

	public boolean useProjectSettings() {
		return useProjectSettingsButton.isSelected();
	}

	private Button createButton(Composite container, int style, int span,
			int indent) {
		Button button = new Button(container, style);
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		gd.horizontalIndent = indent;
		button.setLayoutData(gd);
		return button;
	}

	private void createConfigFileGroup(Composite container) {
		Group springGroup = new Group(container, SWT.NONE);
		springGroup
				.setText(BeansWizardsMessages.NewProjectPage_configFileSettings);
		springGroup.setLayout(initGridLayout(new GridLayout(), true));
		springGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create enable import checkbox
		enableImportButton = SpringUIUtils.createCheckBox(springGroup,
				BeansUIPlugin.getResourceString(ENABLE_IMPORT_LABEL));
		enableImportButton.setSelection(IBeansProject.DEFAULT_IMPORTS_ENABLED);
		enableImportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(validatePage());
			}
		});

		// Create ignore missing namespace handler checkbox
		ignoreMissingNamespaceHandlerButton = SpringUIUtils
				.createCheckBox(
						springGroup,
						BeansUIPlugin
								.getResourceString(IGNORE_MISSING_NAMESPACEHANDLER_LABEL));
		ignoreMissingNamespaceHandlerButton
				.setSelection(BeansCorePlugin.IGNORE_MISSING_NAMESPACEHANDLER_PROPERTY_DEFAULT);
		ignoreMissingNamespaceHandlerButton
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setPageComplete(validatePage());
					}
				});

		// Create suffix text field
		suffixesText = SpringUIUtils.createTextField(springGroup,
				BeansWizardsMessages.NewProjectPage_suffixes);
		suffixesText.setText(IBeansProject.DEFAULT_CONFIG_SUFFIX);
		suffixesText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
	}

	private void createFacetsGroup(Composite container) {
		Group facetsGroup = new Group(container, SWT.NONE);
		facetsGroup.setText(BeansWizardsMessages.NewProjectPage_facetsSettings);
		facetsGroup.setLayout(initGridLayout(new GridLayout(), true));
		facetsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label description = new Label(facetsGroup, SWT.NONE);
		description
				.setText(BeansWizardsMessages.NewProjectPage_projectFacetsDescription);

		enableProjectFacetsButton = createButton(facetsGroup, SWT.CHECK, 1, 0);
		enableProjectFacetsButton
				.setText(BeansWizardsMessages.NewProjectPage_enableProjectFacets);
		enableProjectFacetsButton.setSelection(false);
	}

	private void createNamespaceSettingsGroup(Composite container) {
		Group namespacesGroup = new Group(container, SWT.NONE);
		namespacesGroup
				.setText(BeansWizardsMessages.NewProjectPage_namespacesSettings);
		namespacesGroup.setLayout(initGridLayout(new GridLayout(), true));
		namespacesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		IDialogFieldListener listener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				enableProjectSpecificSettings(((SelectionButtonDialogField) field)
						.isSelected());
			}
		};

		useProjectSettingsButton = new SelectionButtonDialogField(SWT.CHECK);
		useProjectSettingsButton.setDialogFieldListener(listener);
		useProjectSettingsButton
				.setLabelText(BeansWizardsMessages.NewProjectPage_enableProjectSettings);
		useProjectSettingsButton.doFillIntoGrid(namespacesGroup, 1);
		LayoutUtil.setHorizontalGrabbing(useProjectSettingsButton
				.getSelectionButton(null));

		LayoutUtil.setHorizontalSpan(
				useProjectSettingsButton.getSelectionButton(null), 2);

		Label horizontalLine = new Label(namespacesGroup, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		horizontalLine.setFont(namespacesGroup.getFont());

		Preferences prefs = BeansCorePlugin.getDefault().getPluginPreferences();
		boolean versionClasspath = prefs
				.getBoolean(BeansCorePlugin.NAMESPACE_DEFAULT_FROM_CLASSPATH_ID);
		boolean useClasspath = prefs
				.getBoolean(BeansCorePlugin.LOAD_NAMESPACEHANDLER_FROM_CLASSPATH_ID);
		boolean disableCachingNamespaces = prefs.getBoolean(BeansCorePlugin.DISABLE_CACHING_FOR_NAMESPACE_LOADING_ID);

		versionCheckbox = createButton(namespacesGroup, SWT.CHECK, 1,
				convertHorizontalDLUsToPixels(5));
		versionCheckbox
				.setText(BeansWizardsMessages.NewProjectPage_highestXsdVersion);
		versionCheckbox.setSelection(versionClasspath);

		classpathCheckbox = createButton(namespacesGroup, SWT.CHECK, 1,
				convertHorizontalDLUsToPixels(5));
		classpathCheckbox
				.setText(BeansWizardsMessages.NewProjectPage_loadXsdsFromClasspath);
		classpathCheckbox.setSelection(useClasspath);
		classpathCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				disableNamespaceCachingCheckbox.setEnabled(classpathCheckbox.getSelection());
			}
		});
		
		disableNamespaceCachingCheckbox = createButton(namespacesGroup, SWT.CHECK, 1, 
				convertHorizontalDLUsToPixels(15));
		disableNamespaceCachingCheckbox
				.setText(BeansWizardsMessages.NewProjectPage_disableNamespaceCaching);
		disableNamespaceCachingCheckbox.setSelection(disableCachingNamespaces);
		disableNamespaceCachingCheckbox.setEnabled(useClasspath);

		enableProjectSpecificSettings(false);
	}

	private void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		versionCheckbox.setEnabled(useProjectSpecificSettings);
		classpathCheckbox.setEnabled(useProjectSpecificSettings);
		disableNamespaceCachingCheckbox.setEnabled(useProjectSpecificSettings && classpathCheckbox.getSelection());
	}

	// Copied from NewJavaProjectWizardPageOne so that our margins will match
	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	private boolean isValidSuffix(String suffix) {
		if (suffix.length() == 0) {
			return false;
		}
		return true;
	}

	protected boolean validatePage() {
		String suffixes = suffixesText.getText().trim();
		if (suffixes.length() == 0) {
			setErrorMessage(BeansWizardsMessages.NewProjectPage_noSuffixes);
			return false;
		}
		StringTokenizer tokenizer = new StringTokenizer(suffixes, ",");
		while (tokenizer.hasMoreTokens()) {
			String suffix = tokenizer.nextToken().trim();
			if (!isValidSuffix(suffix)) {
				setErrorMessage(BeansWizardsMessages.NewProjectPage_invalidSuffixes);
				return false;
			}
		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

}
