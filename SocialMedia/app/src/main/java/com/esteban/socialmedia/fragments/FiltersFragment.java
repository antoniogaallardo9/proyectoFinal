package com.esteban.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.activities.FiltersActivity;

public class FiltersFragment extends Fragment {
    View mView;
    CardView mCardViewPs;
    CardView mCardViewXbox;
    CardView mCardViewPc;
    CardView mCardViewNintendo;

    public FiltersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        mCardViewPs = mView.findViewById(R.id.cardViewPs);
        mCardViewXbox = mView.findViewById(R.id.cardViewXbox);
        mCardViewPc = mView.findViewById(R.id.cardViewPc);
        mCardViewNintendo = mView.findViewById(R.id.cardViewNintendo);

        mCardViewPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PS5");
            }
        });
        mCardViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("XBOX");
            }
        });
        mCardViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("NINTENDO");
            }
        });

        mCardViewPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PC");
            }
        });
        return mView;
    }


    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}