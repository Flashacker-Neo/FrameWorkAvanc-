package campus.academy.exofinal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import campus.academy.exofinal.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpaceEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpaceEvent.class);
        SpaceEvent spaceEvent1 = new SpaceEvent();
        spaceEvent1.setId(1L);
        SpaceEvent spaceEvent2 = new SpaceEvent();
        spaceEvent2.setId(spaceEvent1.getId());
        assertThat(spaceEvent1).isEqualTo(spaceEvent2);
        spaceEvent2.setId(2L);
        assertThat(spaceEvent1).isNotEqualTo(spaceEvent2);
        spaceEvent1.setId(null);
        assertThat(spaceEvent1).isNotEqualTo(spaceEvent2);
    }
}
