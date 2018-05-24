package com.asmita.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.asmita.utils.Utility;
import com.asmita.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleEventActivity extends AppCompatActivity implements View.OnClickListener
{
    @BindView(R.id.appTitle) TextView appTitle;
    @BindView(R.id.selectDatetxt) TextView selectDatetxt;
    @BindView(R.id.selectTimetxt) TextView selectTimetxt;
    @BindView(R.id.edtxtMail) EditText edtxtMail;
    @BindView(R.id.edttxtAgenda) EditText edttxtAgenda;
    @BindView(R.id.addEventBtn) Button addEventBtn;
    @BindView(R.id.filterIcon)
    ImageView filterIcon;

    private Calendar calendar;
    private int hour,min;
    private DatabaseReference mFirebaseDatabase;
    private String userId;
    private ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_event_layout);

        /*  Intialization of ButterKnife */
        ButterKnife.bind(this);

        /* Intialization of ProgressDialog */
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setCancelable(false);

        appTitle.setText(getString(R.string.addEvent));

        filterIcon.setVisibility(View.GONE);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month +1, day);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            userId = user.getUid();
        }

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.keepSynced(true);
    }

    @Override//Click listener
    @OnClick({R.id.selectDatetxt,R.id.selectTimetxt,R.id.addEventBtn,R.id.backBtn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.selectDatetxt:

                selectDate();

                break;

            case R.id.selectTimetxt:

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerListener, hour, min, false);
                timePickerDialog.show();

                break;

            case R.id.addEventBtn:

                addEvent();

                break;

            case R.id.backBtn:

                moveToPreviousScreen();

                break;
        }
    }

    private void addEvent()
    {
        String email = edtxtMail.getText().toString();
        String agenda = edttxtAgenda.getText().toString();
        String date = selectDatetxt.getText().toString();
        String time = selectTimetxt.getText().toString();

        Utility.hideKeyboard(this);

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(agenda))
        {
            Toast.makeText(getApplicationContext(), "Enter Agenda!", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();

        if (!TextUtils.isEmpty(userId))
        {
            createUser(agenda, email,date,time);
        }
    }

    private void createUser(String agenda, String email,String date,String time)
    {
        // In real apps this userId should be fetched by implementing firebase auth

        Event event = new Event(email,agenda,date,time);

        mFirebaseDatabase.child(userId).push().setValue(event);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener()
    {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Event event = dataSnapshot.getValue(Event.class);

                // Check for null
                if (event == null)
                {
                    return;
                }

                dismissProgress();

                moveToPreviousScreen();
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                dismissProgress();
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes)
        {
            hour = hourOfDay;
            min = minutes;
            showTime(hour, min);
        }
    };

    private void selectDate()
    {
        DatePickerDialog dialog = new DatePickerDialog(this, date_picker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(getText(R.string.selectDate));
        dialog.show();
    }

    DatePickerDialog.OnDateSetListener date_picker = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            showDate(year, monthOfYear+1, dayOfMonth);
        }
    };

    private void showDate(int year, int month, int day)
    {
        selectDatetxt.setText(Utility.convertDateFormat(String.valueOf(new StringBuilder().append(day).append("/").append(month).append("/").append(year))));
    }

    private void showTime(int hour, int min)
    {
        String format;

        if (hour == 0)
        {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12)
        {
            format = "PM";
        }
        else if (hour > 12)
        {
            hour -= 12;
            format = "PM";
        }
        else
        {
            format = "AM";
        }

        selectTimetxt.setText(new StringBuilder().append(hour).append(" : ").append(min).append(" ").append(format));
    }

    private void dismissProgress()
    {
        if(progress != null)
        {
            progress.dismiss();
            progress.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        moveToPreviousScreen();
    }

    private void moveToPreviousScreen()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
