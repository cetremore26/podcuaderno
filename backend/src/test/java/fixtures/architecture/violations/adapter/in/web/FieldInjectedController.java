package fixtures.architecture.violations.adapter.in.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/** Violates noFieldInjection. */
@RestController
public class FieldInjectedController {

    @Autowired
    private Object dependency;
}
