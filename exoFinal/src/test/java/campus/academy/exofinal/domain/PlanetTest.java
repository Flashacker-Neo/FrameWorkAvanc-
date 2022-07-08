package campus.academy.exofinal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import campus.academy.exofinal.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Planet.class);
        Planet planet1 = new Planet();
        planet1.setId(1L);
        Planet planet2 = new Planet();
        planet2.setId(planet1.getId());
        assertThat(planet1).isEqualTo(planet2);
        planet2.setId(2L);
        assertThat(planet1).isNotEqualTo(planet2);
        planet1.setId(null);
        assertThat(planet1).isNotEqualTo(planet2);
    }
}
