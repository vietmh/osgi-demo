package test.combined;

import org.osgi.service.component.annotations.Component;

@Component
public class TestCombinedImpl implements TestCombinedInterface {

	@Override
	public String getName() {
		return "Test Combined Implementation";
	}
}
