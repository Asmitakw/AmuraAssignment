package com.asmita.activities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginScreenTest
{
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    /*
    Test username
     */
    private static final String TEST_USERNAME = "ash@gmail.com";

    /*
    Test password
     */
    private static final String TEST_PASSWORD = "123456";

    /*
   Test username
    */
    private static final String TEST_USERNAME_WRONG = "XYZ";

    /*
    Test password
     */
    private static final String TEST_PASSWORD_WRONG = "XYZ";

    /*
    Test for desired toast on empty,wrong and correct edit texts
     */

    @Test
    public void attemptLogin() throws Exception
    {
        onView(withId(R.id.btn_login)).perform(click(),closeSoftKeyboard());

        checkToastMsg(R.string.enterEmail);

        onView(withId(R.id.edtxtEmail)).perform(typeText(TEST_USERNAME_WRONG),closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click(),closeSoftKeyboard());

        checkToastMsg(R.string.enterPwd);
        onView(withId(R.id.edtxtPassword)).perform(typeText(TEST_PASSWORD_WRONG),closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click(),closeSoftKeyboard());

        checkToastMsg(R.string.minimum_password);

        onView(withId(R.id.edtxtEmail)).perform(clearText());

        onView(withId(R.id.edtxtEmail)).perform(typeText(TEST_USERNAME),closeSoftKeyboard());

        onView(withId(R.id.edtxtPassword)).perform(clearText());

        onView(withId(R.id.edtxtPassword)).perform(typeText(TEST_PASSWORD),closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
    }

    public void checkToastMsg(int resId)
    {
        LoginActivity activity = mActivityTestRule.getActivity();
        onView(withText(resId)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
