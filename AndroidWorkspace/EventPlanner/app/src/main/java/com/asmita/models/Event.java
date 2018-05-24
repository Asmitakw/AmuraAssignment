package com.asmita.models;


public class Event
{
    public String email;
    public String agenda;
    public String date;
    public String time;

    public Event() {
    }

    public Event(String email, String agenda,String date,String time)
    {
        this.agenda = agenda;
        this.email = email;
        this.date = date;
        this.time = time;
    }
}
