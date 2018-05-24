package com.asmita.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.asmita.adapter.RecyclerViewAdapter;
import com.asmita.models.ContentItem;
import com.asmita.models.Header;
import com.asmita.utils.EventAdapter;
import com.asmita.interfaces.EventClickListener;
import com.asmita.models.Event;
import com.asmita.utils.Utility;
import com.asmita.utils.YearMonthPickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,EventClickListener
{
    static
    {   // To provide support of vector icon below sdk version 21.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.addFab) FloatingActionButton addFab;
    @BindView(R.id.mainRclv) EventAdapter mainRclv;
    @BindView(R.id.rclvEmptyView) TextView rclvEmptyView;
    @BindView(R.id.backBtn) ImageView backBtn;
    @BindView(R.id.filterTxt) TextView filterTxt;
    @BindView(R.id.filterIcon) ImageView filterIcon;

    private ProgressDialog progress;
    private ArrayList<Event> eventsArrayList;
    private RecyclerViewAdapter rclvAdapter;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private Calendar calendar;
    private int filterType = 0;
    private String selectedDate,endDate,endDateEdited,monthStr,selectedDateEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*  Intialization of ButterKnife */
        ButterKnife.bind(this);

        //backBtn.setVisibility(View.GONE);
        backBtn.setBackground(getDrawable(R.drawable.event_planner));
        backBtn.setEnabled(false);

         /* Intialization of ProgressDialog */
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setCancelable(false);

        calendar = Calendar.getInstance();

        mainRclv.setEmptyView(rclvEmptyView);

        /*  Intialization of ArrayList */
        eventsArrayList = new ArrayList<>();

         /*  Intialization of RecyclerView Adapter */
        rclvAdapter = new RecyclerViewAdapter(eventsArrayList);
        mainRclv.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        rclvAdapter.setOnItemClickListener(HomeActivity.this);

        mainRclv.setAdapter(rclvAdapter);

        progress.show();

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null)
        {
            currentUserId = mFirebaseUser.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refereshview();
    }

    private void refereshview()
    {
        mDatabase.keepSynced(true);

        eventsArrayList.clear();

        // Link -> https://stackoverflow.com/questions/41851973/how-to-retrieve-data-from-one-single-userid-firebase-android

        mDatabase.child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (TextUtils.isEmpty(selectedDate))
                {
                    selectedDate = Utility.getTodaysDate();
                }

                if(filterType == 0)
                {
                    filterTxt.setText(selectedDate);
                }
                else if(filterType == 1)
                {
                    String weekDay = selectedDate +" - "+endDateEdited;
                    filterTxt.setText(weekDay);
                }
                else if(filterType == 2)
                {
                    filterTxt.setText(monthStr);
                }

                if(dataSnapshot.exists())
                {
                    Map<String, ArrayList<Event>> map1 = new HashMap<>();

                    for(DataSnapshot userDetails : dataSnapshot.getChildren())
                    {
                        Event event = userDetails.getValue(Event.class);

                        if(filterType == 1)
                        {
                            try
                            {
                                String datePattern = "dd-MMM-yyyy";
                                SimpleDateFormat df = new SimpleDateFormat(datePattern, Locale.US);
                                if(df.parse(event.date).after(df.parse(selectedDateEdit)) && df.parse(event.date).before(df.parse(endDate)))
                                {
                                    if(map1.get(event.date) == null)
                                    {
                                        ArrayList<Event> events = new ArrayList<>();
                                        events.add(event);
                                        map1.put(event.date,events);//event.date+" , "+event.time

                                    }
                                    else
                                    {
                                        map1.get(event.date).add(event);
                                    }
                                }
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(filterType == 0)
                        {
                            if(event.date.equalsIgnoreCase(selectedDate))
                            {
                                if(map1.get(event.time) == null)
                                {
                                    ArrayList<Event> events = new ArrayList<>();
                                    events.add(event);
                                    map1.put(event.time,events);
                                }
                                else
                                {
                                    map1.get(event.time).add(event);
                                }
                            }
                        }
                        else if (filterType == 2)
                        {
                            if(event.date.contains(monthStr))
                            {
                                if(map1.get(event.date) == null)
                                {
                                    ArrayList<Event> events = new ArrayList<>();
                                    events.add(event);
                                    map1.put(event.date,events);

                                }
                                else
                                {
                                    map1.get(event.date).add(event);
                                }
                            }
                        }
                    }

                    //Link --> https://stackoverflow.com/questions/26530685/is-there-an-addheaderview-equivalent-for-recyclerview

                    for(Map.Entry<String, ArrayList<Event>> entry:map1.entrySet())
                    {
                        Header header = new Header();
                        header.setHeader(entry.getKey());
                        eventsArrayList.add(header);

                        for(int i = 0; i< entry.getValue().size();i++)
                        {
                            Event b = entry.getValue().get(i);

                            if(filterType == 0)
                            {
                                if(b.time.equalsIgnoreCase(entry.getKey()))
                                {
                                    ContentItem item = new ContentItem();
                                    item.setAgenda(b.agenda);
                                    item.setEmail(b.email);
                                    item.setDate(b.date);
                                    item.setTime(b.time);

                                    eventsArrayList.add(item);
                                }
                            }
                            else if(filterType == 1 || filterType == 2)
                            {
                                if((b.date).equalsIgnoreCase(entry.getKey()))
                                {
                                    ContentItem item = new ContentItem();
                                    item.setAgenda(b.agenda);
                                    item.setEmail(b.email);
                                    item.setDate(b.date);
                                    item.setTime(b.time);

                                    eventsArrayList.add(item);
                                }
                            }
                        }
                    }

                    if(rclvAdapter != null)
                        rclvAdapter.updateData(filterType);
                }

                dismissProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override//Click listener for FAB Icon
    @OnClick({R.id.addFab,R.id.filterIcon})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addFab:

                Intent intent = new Intent(this,ScheduleEventActivity.class);
                startActivity(intent);
                finish();

                break;

            case R.id.filterIcon:

                filterAlertDialog();

                break;
        }
    }

    private void filterAlertDialog()
    {
        final String[] val = getResources().getStringArray(R.array.filterItemArray);

        final Dialog dialogFilter = new Dialog(this);

        dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogFilter.setContentView(R.layout.filter_doc_dialog);

        ListView listview_profile_photo = dialogFilter.findViewById(R.id.listview_attach);

        listview_profile_photo.setAdapter(new ArrayAdapter<>(this, R.layout.filter_textview, R.id.photo_textview, val));

        listview_profile_photo.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0)
                {
                    filterType = 0;
                    selectDate();
                }
                else if(position == 1)
                {
                    filterType = 1;
                    selectDate();
                }
                else if(position == 2)
                {
                    selectMonthDate();
                }
                dialogFilter.dismiss();
                dialogFilter.cancel();
            }
        });

        dialogFilter.show();
    }

    private void selectMonthDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,4,23);

        YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(this, calendar, new YearMonthPickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onYearMonthSet(int year, int month)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);

                        String datePattern = "dd-MMM-yyyy";
                        SimpleDateFormat df = new SimpleDateFormat(datePattern, Locale.US);

                        String[] parts = df.format(calendar.getTime()).split("-");
                        String first = parts[1];
                        String second = parts[2];

                        monthStr = first +"-"+ second;

                        filterType = 2;

                        refereshview();
                    }
                });
        yearMonthPickerDialog.setMinYear(2000);
        yearMonthPickerDialog.setMaxYear(2020);
        yearMonthPickerDialog.show();
    }

    @Override
    public void onItemClick(View view, int position)
    {}

    private void dismissProgress()
    {
        if(progress != null)
        {
            progress.dismiss();
            progress.cancel();
        }
    }

    private void selectDate()
    {
        DatePickerDialog dialog = new DatePickerDialog(this, date_picker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.setTitle(getText(R.string.Start_Date));

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

            if(filterType == 0)
            {
                selectedDate = Utility.convertDateFormat(String.valueOf(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear+1).append("/").append(year)));

                refereshview();
            }
            else if(filterType == 1)
            {
                selectedDate = Utility.convertDateFormat(String.valueOf(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear+1).append("/").append(year)));

                selectedDateEdit = Utility.convertDateFormat(String.valueOf(new StringBuilder().append(dayOfMonth-1).append("/").append(monthOfYear+1).append("/").append(year)));

                endDate();
            }
        }
    };

    private void endDate()
    {
        DatePickerDialog dialog = new DatePickerDialog(this, endDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        dialog.setTitle(getText(R.string.End_Date));

        dialog.show();
    }

    DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if(filterType == 1)
            {
                endDate = Utility.convertDateFormat(String.valueOf(new StringBuilder().append(dayOfMonth+1).append("/").append(monthOfYear+1).append("/").append(year)));

                endDateEdited = Utility.convertDateFormat(String.valueOf(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear+1).append("/").append(year)));

                refereshview();
            }

        }
    };
}
