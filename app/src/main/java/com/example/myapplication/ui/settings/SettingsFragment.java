package com.example.myapplication.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.ui.solitaire.SolitaireViewModel;

public class SettingsFragment extends Fragment {

    private ImageView imageView;
    private Button changeSizeButton;
    private Button textToSpeechButton;
    private TextView settingsText;
    private SolitaireViewModel solitaireViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_settings.xml layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the shared SolitaireViewModel
        solitaireViewModel = new ViewModelProvider(requireActivity()).get(SolitaireViewModel.class);

        // Find the ImageView, Buttons, and TextView by their IDs
        imageView = view.findViewById(R.id.imageView4);
        changeSizeButton = view.findViewById(R.id.button);
        textToSpeechButton = view.findViewById(R.id.button2); // TTS button
        settingsText = view.findViewById(R.id.settingsText);  // TextView to show TTS state

        // Observe the card size state from the ViewModel
        solitaireViewModel.getIsLargeCard().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLarge) {
                // Update the image based on the card size state
                if (isLarge) {
                    imageView.setImageResource(R.drawable.aaceofspadeslarge);
                    imageView.setTag("large");
                } else {
                    imageView.setImageResource(R.drawable.aaceofspades);
                    imageView.setTag("small");
                }
            }
        });

        // Observe the TTS state from the ViewModel
        solitaireViewModel.getIsTtsEnabled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTtsEnabled) {
                // Update the TextView to show the current TTS state
                settingsText.setText(isTtsEnabled ? "Enabled" : "Disabled");
            }
        });

        // Set up the button click listener to toggle card size
        changeSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getTag().equals("small")) {
                    imageView.setImageResource(R.drawable.aaceofspadeslarge); // Switch to large Ace of Spades
                    imageView.setTag("large");
                    solitaireViewModel.setCardSizeLarge(true); // Save the large state in the ViewModel
                } else {
                    imageView.setImageResource(R.drawable.aaceofspades); // Switch to small Ace of Spades
                    imageView.setTag("small");
                    solitaireViewModel.setCardSizeLarge(false); // Save the small state in the ViewModel
                }
            }
        });

        // Set up the TTS button click listener to toggle Text-To-Speech state
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the TTS enabled/disabled state
                boolean currentTtsState = solitaireViewModel.getIsTtsEnabled().getValue() != null
                        && solitaireViewModel.getIsTtsEnabled().getValue();
                solitaireViewModel.setTtsEnabled(!currentTtsState);
            }
        });
    }
}