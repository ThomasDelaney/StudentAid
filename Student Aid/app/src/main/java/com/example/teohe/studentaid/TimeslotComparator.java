package com.example.teohe.studentaid;

import java.util.Comparator;

/**
 * Created by Thomas on 19/11/2017.
 */

public class TimeslotComparator implements Comparator<Timeslot>
{
    @Override
    public int compare(Timeslot timeslot1, Timeslot timeslot2)
    {
        if (timeslot1.getSlot() > timeslot2.getSlot())
        {
            return 1;
        }
        else if (timeslot1.getSlot() < timeslot2.getSlot())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}