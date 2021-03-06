package com.android.group.view;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.group.R;
import com.android.group.model.bindle.BindleBusiness;
import com.android.group.viewmodel.GroupViewModel;
import com.android.group.viewmodel.NetworkViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

public final class StartGroupFragment extends Fragment
  implements NetworkViewModel.OnVenueSelectedListener,
  NetworkViewModel.OnCategorySelectedListener {

    public static final String GROUP_PREFS = "GROUP";
    public static final String CURRENT_GROUP_KEY = "current_group";

    private NetworkViewModel networkViewModel;
    private GroupViewModel groupViewModel;
    private OnFragmentInteractionListener interactionListener;
    private OnFragmentInteractionCompleteListener interactionCompleteListener;
    private TextView locationTextView;
    private BindleBusiness userSelectedBindleBusiness;
    private String currentCategory;

    public static StartGroupFragment newInstance() {
        return new StartGroupFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Context does not implement OnFragmentInteractionListener");
        }

        if (context instanceof OnFragmentInteractionCompleteListener) {
            interactionCompleteListener = (OnFragmentInteractionCompleteListener) context;
        } else {
            throw new RuntimeException("Context does not implement OnFragmentInteractionCompleteListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkViewModel = NetworkViewModel.getSingleInstance();
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        networkViewModel.setVenueSelectedListener(this);
        networkViewModel.setCategorySelectedListener(this);
        return inflater.inflate(R.layout.fragment_start_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.<ViewGroup>findViewById(R.id.add_location_container)
          .setOnClickListener(v -> interactionListener.inflateAddLocationFragment());
        locationTextView = view.findViewById(R.id.add_location_text_view);
        setStartButtonOnClickListener(view);
    }

    @Override
    public void bindleBusinessSelected(BindleBusiness bindleBusiness) {
        userSelectedBindleBusiness = bindleBusiness;
        locationTextView.setText(userSelectedBindleBusiness.getVenue().getLocation().getAddress());
    }

    @Override
    public void categorySelected(String category) {
        currentCategory = category;
    }

    private void setStartButtonOnClickListener(@NonNull final View view) {
        view.<Button>findViewById(R.id.start_group_button).setOnClickListener(v -> {
            final String groupName =
              view.<EditText>findViewById(R.id.group_name_edit_text)
                .getText().toString();
            final String groupDescription =
              view.<EditText>findViewById(R.id.group_description_edit_text)
                .getText().toString();
            if (!groupName.equals("")) {
                if (!groupDescription.equals("")) {
                    if (currentCategory != null) {
                        if (userSelectedBindleBusiness != null) {
                            getActivity().getSharedPreferences(GROUP_PREFS,
                              Context.MODE_PRIVATE).edit().putString(CURRENT_GROUP_KEY, groupName).apply();
                            groupViewModel.createGroup(groupName,
                              userSelectedBindleBusiness,
                              groupDescription,
                              currentCategory, o ->
                                interactionCompleteListener
                                  .goToGroupChatFragment(groupViewModel.getCurrentGroup())
                            );
                        } else {
                            makeToast("Select a Location");
                        }
                    } else {
                        makeToast("Please select a category.");
                    }
                } else {
                    makeToast("Provide a Group Description");
                }
            } else {
                makeToast("Provide a Group Name");
            }
        });
    }

    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void inflateAddLocationFragment();
    }
}
