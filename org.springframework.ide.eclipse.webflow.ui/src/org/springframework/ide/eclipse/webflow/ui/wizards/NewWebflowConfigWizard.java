/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ide.eclipse.webflow.ui.wizards;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.springframework.ide.eclipse.beans.ui.BeansUIImages;
import org.springframework.ide.eclipse.beans.ui.BeansUIPlugin;
import org.springframework.ide.eclipse.ui.SpringUIUtils;
import org.springframework.ide.eclipse.webflow.core.internal.model.WebflowConfig;
import org.springframework.ide.eclipse.webflow.core.model.IWebflowConfig;
import org.springframework.ide.eclipse.webflow.core.model.IWebflowProject;
import org.springframework.ide.eclipse.webflow.ui.Activator;

/**
 * @author Christian Dupuis
 * @since 2.0
 */
public class NewWebflowConfigWizard extends Wizard implements INewWizard {

	private NewWebflowConfigFilePage mainPage;

	private LinkToBeansConfigWizardPage linkPage;

	private IStructuredSelection selection;

	private IWebflowConfig newConfig;

	public NewWebflowConfigWizard() {
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	/**
	 * Returns the newly created beans config.
	 * @return the created beans config, or <code>null</code> if config not
	 * created
	 */
	public IWebflowConfig getNewConfig() {
		return newConfig;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		setNeedsProgressMonitor(true);
		setWindowTitle("Create Spring Web Flow definition");
		setDefaultPageImageDescriptor(BeansUIImages.DESC_WIZ_CONFIG);
	}

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new NewWebflowConfigFilePage("webflowNewConfigPage",
				selection);
		linkPage = new LinkToBeansConfigWizardPage("webflowLinkConfigPage");
		// mainPage.setTitle(BeansWizardsMessages.NewConfig_title);
		// mainPage.setDescription(BeansWizardsMessages.NewConfig_description);
		addPage(mainPage);
		addPage(linkPage);
	}

	@Override
	public boolean performFinish() {
		createNewConfig();
		if (newConfig == null) {
			return false;
		}

		BasicNewResourceWizard.selectAndReveal(newConfig.getResource(),
				BeansUIPlugin.getActiveWorkbenchWindow());
		SpringUIUtils.openInEditor(newConfig.getResource(), -1);
		return true;
	}

	private void createNewConfig() {

		// create the new Spring project operation
		IFile file = mainPage.createNewFile();
		newConfig = new WebflowConfig(getProject(file));
		newConfig.setResource(file);
		String name = linkPage.getName();
		if (name != null && !"".equals(name)) {
			newConfig.setName(name);
		}
		newConfig.setBeansConfigs(linkPage.getBeansConfigs());
		
		IWebflowProject wfp = getProject(file);
		List<IWebflowConfig> configs = wfp.getConfigs();
		configs.add(newConfig);
		
		wfp.setConfigs(configs);
	}

	private IWebflowProject getProject(IFile file) {
		return org.springframework.ide.eclipse.webflow.core.Activator
				.getModel().getProject(file.getProject());
	}
}