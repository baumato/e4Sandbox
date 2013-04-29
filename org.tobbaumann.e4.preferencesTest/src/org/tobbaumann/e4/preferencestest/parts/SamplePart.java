/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.tobbaumann.e4.preferencestest.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

public class SamplePart {

	private static final String PREFERENCES_NODE = "org.tobbaumann.e4.preferencesTest";
	private static final String PREFERENCE_NAME = "thePref";

	private Label lblPrefValue;

	// gets updated via dependency injection in #updatePrefValue
	private String prefValue = "Set in part";

	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		Label l = new Label(parent, SWT.NONE);
		l.setText("Preference Value: ");
		lblPrefValue = new Label(parent, SWT.NONE);
		lblPrefValue.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		lblPrefValue.setText(prefValue);

		final Text txtNewPrefValue = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		txtNewPrefValue.setText("Set by TextField");

		Button btnUpdateInInstanceScope = new Button(parent, SWT.PUSH);
		btnUpdateInInstanceScope.setText("Update in InstanceScope");
		btnUpdateInInstanceScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InstanceScope.INSTANCE.getNode(PREFERENCES_NODE).put(PREFERENCE_NAME, txtNewPrefValue.getText());
			}
		});

		Button btnUpdateInConfigurationScope = new Button(parent, SWT.PUSH);
		btnUpdateInConfigurationScope.setText("Update in ConfigurationScope");
		btnUpdateInConfigurationScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode(PREFERENCES_NODE);
				node.put(PREFERENCE_NAME, txtNewPrefValue.getText());
				try {
					node.flush();
				} catch (BackingStoreException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Inject
	@Optional
	public void updatePrefValue(@Preference(value = PREFERENCE_NAME) String prefValue) {
		System.out.println("updatePrefValue with '" + prefValue + "'.");
		this.prefValue = prefValue;
		if (lblPrefValue != null && !lblPrefValue.isDisposed()) {
			lblPrefValue.setText(prefValue);
		}
	}

	@Focus
	public void setFocus() {
		lblPrefValue.setFocus();
	}
}
