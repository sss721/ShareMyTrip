package com.example.shweta.sharemytrip;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Shweta on 4/6/2016.
 */
public class BottomView extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflator, @Nullable ViewGroup containter, @Nullable Bundle savedInstanceState){
        View view = inflator.inflate(R.layout.bottom_section,containter,false);
        return view;
    }
}
