package de.rockware.aem.rat.core.impl.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.event.Event;

import java.util.HashMap;
import java.util.Map;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Event handlers and jpb consumers don't test well with the AEMContext.
 */
@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class CreateContentPageEventHandlerImplTest {

    private CreateContentPageEventHandlerImpl handler;

    @BeforeEach
    void setUp() {
        handler = new CreateContentPageEventHandlerImpl();
    }

    @Test
    public void handleEvent() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("pageEvent", null);
        assertThrows(NullPointerException.class, () -> {
            handler.handleEvent(new Event("de/rockware/aem/tenants", properties));
        });
    }

    @Test
    public void processJob() {
        assertThrows(NullPointerException.class, () -> {
            handler.process(null);
        });
    }

}