package fixtures.architecture.sample.domain.exception;

import com.podcuaderno.shared.domain.exception.ResourceNotFoundException;

public class SampleNotFoundException extends ResourceNotFoundException {

    public SampleNotFoundException() {
        super("sample not found");
    }
}
