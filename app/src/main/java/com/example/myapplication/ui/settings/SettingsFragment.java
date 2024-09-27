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
import com.example.myapplication.ui.spider.SpiderSolitaireViewModel;

public class SettingsFragment extends Fragment {

    private ImageView imageView;
    private Button changeSizeButton;
    private Button textToSpeechButton;
    private TextView settingsText;
    private SolitaireViewModel solitaireViewModel;
    private SpiderSolitaireViewModel spiderSolitaireViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_settings.xml layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the shared SolitaireViewModel and SpiderSolitaireViewModel
        solitaireViewModel = new ViewModelProvider(requireActivity()).get(SolitaireViewModel.class);
        spiderSolitaireViewModel = new ViewModelProvider(requireActivity()).get(SpiderSolitaireViewModel.class);

        // Find the ImageView, Buttons, and TextView by their IDs
        imageView = view.findViewById(R.id.imageView4);
        changeSizeButton = view.findViewById(R.id.button);
        textToSpeechButton = view.findViewById(R.id.button2); // TTS button
        settingsText = view.findViewById(R.id.settingsText);  // TextView to show TTS state

        // Observe the card size state from the ViewModel (Shared between Solitaire and Spider Solitaire)
        Observer<Boolean> cardSizeObserver = new Observer<Boolean>() {
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
        };

        // Listen for card size changes in both Solitaire and Spider Solitaire
        solitaireViewModel.getIsLargeCard().observe(getViewLifecycleOwner(), cardSizeObserver);

        // Observe the TTS state from the ViewModel (Shared between Solitaire and Spider Solitaire)
        Observer<Boolean> ttsObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTtsEnabled) {
                // Update the TextView to show the current TTS state
                settingsText.setText(isTtsEnabled ? "Enabled" : "Disabled");
            }
        };

        // Listen for TTS state changes in both Solitaire and Spider Solitaire
        solitaireViewModel.getIsTtsEnabled().observe(getViewLifecycleOwner(), ttsObserver);
        spiderSolitaireViewModel.getIsTtsEnabled().observe(getViewLifecycleOwner(), ttsObserver);

        // Set up the button click listener to toggle card size
        changeSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSmall = imageView.getTag().equals("small");
                if (isSmall) {
                    imageView.setImageResource(R.drawable.aaceofspadeslarge); // Switch to large Ace of Spades
                    imageView.setTag("large");
                } else {
                    imageView.setImageResource(R.drawable.aaceofspades); // Switch to small Ace of Spades
                    imageView.setTag("small");
                }

                // Save the large/small state in both ViewModels
                solitaireViewModel.setCardSizeLarge(isSmall);
            }
        });

        // Set up the TTS button click listener to toggle Text-To-Speech state
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the TTS enabled/disabled state
                boolean currentTtsState = solitaireViewModel.getIsTtsEnabled().getValue() != null
                        && solitaireViewModel.getIsTtsEnabled().getValue();

                // Update the TTS state in both Solitaire and Spider Solitaire ViewModels
                solitaireViewModel.setTtsEnabled(!currentTtsState);
                spiderSolitaireViewModel.setTtsEnabled(!currentTtsState);
            }
        });
    }
}
