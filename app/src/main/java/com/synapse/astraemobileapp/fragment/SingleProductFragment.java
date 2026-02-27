package com.synapse.astraemobileapp.fragment;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.FragmentSingleProductBinding;

public class SingleProductFragment extends Fragment {

    private FragmentSingleProductBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSingleProductBinding.inflate(inflater, container, false);

        //make line through text for estimate price
        binding.singleProductEstimatePrice.setPaintFlags(
                binding.singleProductEstimatePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}