package it.polito.gruppo2.mamk.lab3.utils;

import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealPosition;

import java.util.Comparator;

public class PositionsTimestampComparator implements Comparator<RealPosition> {
    @Override
    public int compare(RealPosition el1, RealPosition el2){
        if(el1.getTimestamp() > el2.getTimestamp()) return 1;
        else if (el1.getTimestamp() == el2.getTimestamp()) return 0;
        else return -1;
    }
}