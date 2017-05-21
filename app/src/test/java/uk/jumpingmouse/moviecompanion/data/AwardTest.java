package uk.jumpingmouse.moviecompanion.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

    private Award mAward;

    // By default, expect no exceptions.
    // thrown must be public.
    @SuppressWarnings("WeakerAccess")
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        mAward = Award.builder()
                .awardDate("170512")
                .category(Award.CATEGORY_MOVIE)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build();
    }

    @After
    public void tearDown() {
        mAward = null;
    }

    /**
     * Test that builder works with valid values.
     */
    @Test
    public void builder() {
        // test that the fields return the expected values
        assertEquals("170512", mAward.getAwardDate());
        assertEquals(Award.CATEGORY_MOVIE, mAward.getCategory());
        assertEquals("tt4016934", mAward.getImdbId());
        assertEquals(REVIEW, mAward.getReview());
    }

    /**
     * Test that all fields are mandatory, i.e. if one is null, an exception is thrown.
     */
    @Test
    public void builderMandatoryFieldsAreNull() {
        // We expect an exception to be thrown
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Missing required properties: awardDate category imdbId review");

        @SuppressWarnings({"unused", "UnusedAssignment", "ConstantConditions"})
        Award award = Award.builder()
                .awardDate(null)
                .category(null)
                .imdbId(null)
                .review(null)
                .build();
    }

    /**
     * Test the equals() method.
     */
    @Test
    public void testEquals() {
        // test that an award equals itself
        //noinspection EqualsWithItself
        assertTrue(mAward.equals(mAward));

        // test that an award does not equal a String
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(mAward.equals(mAward.toString()));

        // test that building with the same parameter values results in equals(...) returning true
        assertTrue(Award.builder()
                .awardDate("170512")
                .category(Award.CATEGORY_MOVIE)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build()
                .equals(mAward));

        // test that building with a different awardDate results in equals(...) returning false
        assertFalse(Award.builder()
                .awardDate("170519")
                .category(Award.CATEGORY_MOVIE)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build()
                .equals(mAward));
    }

    @Test
    public void testHashcode() {
        assertTrue(mAward.hashCode() > 1);
        // test that the hashcode is different when the award is built with a different awardDate
        assertTrue(mAward.hashCode() != Award.builder()
                .awardDate("170519")
                .category(Award.CATEGORY_MOVIE)
                .imdbId("tt4016934")
                .review(REVIEW)
                .build()
                .hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(
                "Award{awardDate=170512, category=" + Award.CATEGORY_MOVIE +
                        ", imdbId=tt4016934, review=" + REVIEW + "}",
                mAward.toString());
    }

}