package uk.jumpingmouse.moviecompanion.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Award model class.
 * @author Edmund Johnson
 */
public class AwardTest {
    private static final String ID = "award_pk_1";
    private static final String MOVIE_ID = "4016934";
    private static final String AWARD_DATE_1 = "170512";
    private static final String AWARD_DATE_2 = "170519";
    private static final String REVIEW =
            "A really smart satire on what's been referred to as post-racial America.";
    private static final int DISPLAY_ORDER = 2;

    private Award mAward;

    // By default, expect no exceptions.
    // thrown must be public.
    @SuppressWarnings("WeakerAccess")
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        mAward = Award.builder()
                .id(ID)
                .movieId(MOVIE_ID)
                .awardDate(AWARD_DATE_1)
                .category(Award.CATEGORY_MOVIE)
                .review(REVIEW)
                .displayOrder(DISPLAY_ORDER)
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
        assertEquals(ID, mAward.getId());
        assertEquals(MOVIE_ID, mAward.getMovieId());
        assertEquals(AWARD_DATE_1, mAward.getAwardDate());
        assertEquals(Award.CATEGORY_MOVIE, mAward.getCategory());
        assertEquals(REVIEW, mAward.getReview());
        assertEquals(DISPLAY_ORDER, mAward.getDisplayOrder());
    }

    /**
     * Test that all fields are mandatory, i.e. if one is null, an exception is thrown.
     */
    @Test
    public void builderMandatoryFieldsAreNull() {
        // We expect an exception to be thrown
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Missing required properties: id movieId awardDate category review");

        @SuppressWarnings({"unused", "UnusedAssignment", "ConstantConditions"})
        Award award = Award.builder()
                .id(null)
                .movieId(null)
                .awardDate(null)
                .category(null)
                .review(null)
                .displayOrder(0)
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

        // test that building with the same ID but different field values
        // results in equals(...) returning true
        assertTrue(Award.builder()
                .id(ID)
                .movieId("movie id")
                .awardDate(AWARD_DATE_2)
                .category(Award.CATEGORY_DVD)
                .review(REVIEW + " extra")
                .displayOrder(DISPLAY_ORDER + 1)
                .build()
                .equals(mAward));

        // test that building with a different id results in equals(...) returning false
        assertFalse(Award.builder()
                .id("123")
                .movieId(MOVIE_ID)
                .awardDate(AWARD_DATE_1)
                .category(Award.CATEGORY_MOVIE)
                .review(REVIEW)
                .displayOrder(DISPLAY_ORDER)
                .build()
                .equals(mAward));
    }

    @Test
    public void testHashcode() {
        //assertTrue(mAward.hashCode() > 1); // no, hashCode can be negative

        // test that the hashcode is the same when the award is built with
        // the same id but a different awardDate
        assertEquals(mAward.hashCode(),
                Award.builder()
                .id(ID)
                .movieId(MOVIE_ID)
                .awardDate(AWARD_DATE_2)
                .category(Award.CATEGORY_MOVIE)
                .review(REVIEW)
                .displayOrder(DISPLAY_ORDER)
                .build()
                .hashCode());

        // test that the hashcode is different when the award is built with
        // a different id and all other fields the same
        assertNotEquals(mAward.hashCode(),
                Award.builder()
                        .id("123")
                        .movieId(MOVIE_ID)
                        .awardDate(AWARD_DATE_1)
                        .category(Award.CATEGORY_MOVIE)
                        .review(REVIEW)
                        .displayOrder(DISPLAY_ORDER)
                        .build()
                        .hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(
                "Award{id=" + ID
                        + ", movieId=" + MOVIE_ID
                        + ", awardDate=" + AWARD_DATE_1
                        + ", category=" + Award.CATEGORY_MOVIE
                        + ", review=" + REVIEW
                        + ", displayOrder=" + DISPLAY_ORDER
                        + "}",
                mAward.toString());
    }

}