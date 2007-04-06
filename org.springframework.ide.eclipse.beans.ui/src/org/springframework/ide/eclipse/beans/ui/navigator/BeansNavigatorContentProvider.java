/*******************************************************************************
 * Copyright (c) 2005, 2007 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.ui.navigator;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansModelUtils;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;
import org.springframework.ide.eclipse.beans.ui.BeansUIPlugin;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelContentProvider;
import org.springframework.ide.eclipse.core.SpringCore;
import org.springframework.ide.eclipse.core.SpringCoreUtils;
import org.springframework.ide.eclipse.core.model.IModelElement;
import org.springframework.ide.eclipse.core.model.ModelChangeEvent;

/**
 * This class is a content provider for the {@link CommonNavigator} which knows
 * about the beans core model's {@link IModelElement} elements.
 * 
 * @author Torsten Juergeleit
 * @author Christian Dupuis
 */
public class BeansNavigatorContentProvider extends BeansModelContentProvider
		implements ICommonContentProvider {

	public static final String PROJECT_EXPLORER_CONTENT_PROVIDER_ID =
			BeansUIPlugin.PLUGIN_ID + ".navigator.projectExplorerContent";
	public static final String SPRING_EXPLORER_CONTENT_PROVIDER_ID =
			BeansUIPlugin.PLUGIN_ID + ".navigator.springExplorerContent";

	private String providerID;

	@Override
	public Object[] getElements(Object inputElement) {
		if (providerID.equals(PROJECT_EXPLORER_CONTENT_PROVIDER_ID)) {
			return SpringCoreUtils.getSpringProjects().toArray();
		}
		return super.getElements(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {
		IProject project = SpringCoreUtils.getAdapter(element, IProject.class);
		if (project != null) {
			return SpringCoreUtils.isSpringProject(project);
		}
		return super.hasChildren(element);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		IProject project = SpringCoreUtils.getAdapter(parentElement,
				IProject.class);
		if (project != null) {
			IBeansProject beansProject = BeansCorePlugin.getModel().getProject(
					project);
			if (beansProject != null) {
				return getProjectChildren(beansProject, false);
			}
			return IModelElement.NO_CHILDREN;
		}
		return super.getChildren(parentElement);
	}

	@Override
	public void elementChanged(ModelChangeEvent event) {
		IModelElement element = event.getElement();

		if (element instanceof IBeansProject) {
			IProject project = ((IBeansProject) element).getProject();
			if (providerID.equals(PROJECT_EXPLORER_CONTENT_PROVIDER_ID)) {
				refreshViewerForElement(project);
			} else if (providerID.equals(SPRING_EXPLORER_CONTENT_PROVIDER_ID)) {
				refreshViewerForElement(SpringCore.getModel().getProject(
						project));
			} else {
				super.elementChanged(event);
			}
		} else if (element instanceof IBeansConfig) {
			IBeansConfig config = (IBeansConfig) element;
			refreshViewerForElement(config.getElementResource());

			// For a changed Spring beans config in the Eclipse Project Explorer
			// refresh all corresponding bean classes
			if (providerID.equals(PROJECT_EXPLORER_CONTENT_PROVIDER_ID)) {
				refreshBeanClasses(config);
			}
		} else {
			super.elementChanged(event);
		}
	}

	/**
	 * Refreshes the config file and all bean classes of a given beans config
	 */
	protected void refreshBeanClasses(IBeansConfig config) {
		Set<String> classes = config.getBeanClasses();
		for (String clazz : classes) {
			IType type = BeansModelUtils.getJavaType(config
					.getElementResource().getProject(), clazz);
			if (type != null) {
				refreshViewerForElement(type);
			}
		}
	}

	public void init(ICommonContentExtensionSite config) {
		providerID = config.getExtension().getId();
	}

	public void saveState(IMemento memento) {
	}

	public void restoreState(IMemento memento) {
	}

	@Override
	public String toString() {
		return String.valueOf(providerID);
	}
}