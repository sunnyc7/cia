/*
 * Copyright 2014 James Pether Sörling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.web.impl.ui.application.views.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.hack23.cia.web.impl.ui.application.action.PageActionEventHelper;
import com.hack23.cia.web.impl.ui.application.views.common.pagelinks.PageLinkFactory;
import com.vaadin.navigator.View;
import com.vaadin.ui.Panel;

/**
 * The Class AbstractView.
 */
public abstract class AbstractView extends Panel implements View {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The page link factory. */
	@Autowired
	protected transient PageLinkFactory pageLinkFactory;

	/** The page action event helper. */
	@Autowired
	protected transient PageActionEventHelper pageActionEventHelper;

	/**
	 * Instantiates a new abstract view.
	 */
	protected AbstractView() {
		super();
	}

}
