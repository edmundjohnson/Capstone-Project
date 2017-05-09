package uk.jumpingmouse.moviecompanion.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Award model class.
 * @author Edmund Johnson
 */
public class AwardTest {
    private static final String REVIEW =
            "A really smart satire on what's been referred to as post-racial America.";
    private Award award;

    @Before
    public void setUp() {
        award = Award.builder()
                .awardDate("170512")
                .category(Award.CATEGORY_FILM)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build();
    }

    @After
    public void tearDown() {
        award = null;
    }

    @Test
    public void builder() {

        // test that the fields return the expected values
        assertEquals("170512", award.awardDate());
        assertEquals(Award.CATEGORY_FILM, award.category());
        assertEquals("tt4016934", award.imdbId());
        assertEquals(REVIEW, award.review());
    }

    @Test
    public void testEquals() {
        // test that building with the same parameter values results in equals(...) returning true
        assertTrue(Award.builder()
                .awardDate("170512")
                .category(Award.CATEGORY_FILM)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build()
                .equals(award));

        // test that building with a different awardDate results in equals(...) returning false
        assertFalse(Award.builder()
                .awardDate("170519")
                .category(Award.CATEGORY_FILM)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build()
                .equals(award));
    }

    @Test
    public void testToString() {
        assertEquals(
                "Award{awardDate=170512, category=" + Award.CATEGORY_FILM +
                        ", imdbId=tt4016934, review=" + REVIEW + "}",
                award.toString());
    }

}