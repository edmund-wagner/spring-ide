/*******************************************************************************
 * Copyright (c) 2007 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.webflow.ui.graph.properties;

import org.eclipse.jface.viewers.IFilter;
import org.springframework.ide.eclipse.webflow.core.model.IAction;
import org.springframework.ide.eclipse.webflow.ui.graph.parts.AbstractStatePart;
import org.springframework.ide.eclipse.webflow.ui.graph.parts.StatePart;

/**
 * @author Christian Dupuis
 * @since 2.0
 */
public class ActionNameSectionFilter implements IFilter {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(Object toTest) {
		return (toTest instanceof StatePart && ((AbstractStatePart) toTest)
				.getModel() instanceof IAction);
	}
}
