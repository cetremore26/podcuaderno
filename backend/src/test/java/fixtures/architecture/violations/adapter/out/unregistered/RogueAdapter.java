package fixtures.architecture.violations.adapter.out.unregistered;

import fixtures.architecture.violations.application.service.NotifyUsersHelper;

/** Violates cleanLayers: an adapter package that the onion architecture does not declare accesses the application. */
public class RogueAdapter {

    private final NotifyUsersHelper helper = new NotifyUsersHelper();

    public NotifyUsersHelper helper() {
        return helper;
    }
}
