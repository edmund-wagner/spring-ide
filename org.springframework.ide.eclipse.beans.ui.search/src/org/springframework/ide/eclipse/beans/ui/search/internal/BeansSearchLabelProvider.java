/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.ide.eclipse.beans.ui.search.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.springframework.ide.eclipse.beans.ui.BeansUILabels;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelLabelProvider;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelLabels;
import org.springframework.ide.eclipse.core.model.IModelElement;

/**
 * This {@link ILabelProvider}�delegates to the {@link BeansModelLabelProvider}
 * and appends the {@link IModelElement}'s path to the label.
 * 
 * @author Torsten Juergeleit
 * @since 2.0
 */
public class BeansSearchLabelProvider extends BeansModelLabelProvider {

	public BeansSearchLabelProvider(boolean isDecorating) {
		super(isDecorating);
	}	

	@Override
	protected String getBaseText(Object element) {
		if (element instanceof IModelElement) {
			StringBuffer buffer = new StringBuffer(super.getText(element));
			buffer.append(BeansUILabels.CONCAT_STRING);
			BeansModelLabels.appendElementPathLabel((IModelElement) element,
					BeansUILabels.DESCRIPTION, buffer);
			return buffer.toString();
		}
		return super.getBaseText(element);
	}
}