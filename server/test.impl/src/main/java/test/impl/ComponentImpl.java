package test.impl;

import org.osgi.service.component.annotations.Component;

import test.api.ProviderInterface;

@Component
public class ComponentImpl implements ProviderInterface {

	@Override
	public String getName() {
		return "Test Implementation";
	}
}
