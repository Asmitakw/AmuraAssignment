package com.asmita.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest
{
    @Rule
    public ActivityTestRule<HomeActivity> homeActivity = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void clickonFABIcon()
    {
        onView(withId(R.id.addFab)).perform(click());
    }
}
