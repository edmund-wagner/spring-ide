/*
 * Copyright 2002-2004 the original author or authors.
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

package org.springframework.ide.eclipse.beans.ui.views.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.springframework.ide.eclipse.beans.ui.BeansUIPlugin;
import org.springframework.ide.eclipse.beans.ui.properties.ConfigurationPropertyPage;
import org.springframework.ide.eclipse.beans.ui.views.BeansView;
import org.springframework.ide.eclipse.beans.ui.views.model.BeanNode;
import org.springframework.ide.eclipse.beans.ui.views.model.ConfigNode;
import org.springframework.ide.eclipse.beans.ui.views.model.ConfigSetNode;
import org.springframework.ide.eclipse.beans.ui.views.model.INode;
import org.springframework.ide.eclipse.beans.ui.views.model.ProjectNode;
import org.springframework.ide.eclipse.beans.ui.views.model.PropertyNode;
import org.springframework.ide.eclipse.ui.SpringUIUtils;

public class OpenPropertiesAction extends Action {

	private static final String PREFIX = "View.OpenPropertiesAction.";
	private static final String TITLE = "PropertiesPage.title";

    private BeansView view;

	public OpenPropertiesAction(BeansView view) {
		super(BeansUIPlugin.getResourceString(PREFIX + "label"));
		setToolTipText(BeansUIPlugin.getResourceString(PREFIX + "tooltip"));
		this.view = view;
    }

	public void run() {
		IProject project = null;
		int block = 0;
		INode node = (INode) ((IStructuredSelection)
							 view.getViewer().getSelection()).getFirstElement();
		if (node instanceof ProjectNode) {
			project = ((ProjectNode) node).getProject().getProject();
		} else if (node instanceof ConfigNode) {
			project = ((ConfigNode)
							  node).getProjectNode().getProject().getProject();
		} else if (node instanceof ConfigSetNode) {
			project = ((ConfigSetNode)
							  node).getProjectNode().getProject().getProject();
			block = 1;
		} else if (node instanceof BeanNode) {
			project = ((BeanNode) node).getConfigNode().getProjectNode().
													 getProject().getProject();
		} else if (node instanceof PropertyNode) {
			project = ((PropertyNode) node).getConfigNode().getProjectNode().
													 getProject().getProject();
		}

		// Show project's property page
		if (project != null) {
			String title = BeansUIPlugin.getResourceString(TITLE) +
															  project.getName();
			IPreferencePage page = new ConfigurationPropertyPage(project,
																 block);
			SpringUIUtils.showPreferencePage(ConfigurationPropertyPage.ID, page,
											title);
		}
	}
}
