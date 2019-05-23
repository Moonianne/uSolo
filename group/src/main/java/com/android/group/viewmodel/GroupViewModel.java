package com.android.group.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.android.group.model.Venue;
import com.android.group.model.firebase.Group;
import com.android.group.respository.GroupRepository;

public final class GroupViewModel extends ViewModel {

    private static final String TAG = "GroupViewModel";
    private final GroupRepository repository = new GroupRepository("testGroup", "Pursuit Group");
    private Group currentGroup;

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void createGroup(String groupName,
                            Venue userSelectedVenue,
                            String groupDescription) {
        currentGroup = new Group.Builder(groupName,
          userSelectedVenue.getCategories()[0].getPluralName(), groupDescription)
          .setiD("")
          .setLat(userSelectedVenue.getLocation().getLat())
          .setLng(userSelectedVenue.getLocation().getLng())
          .setUserList(null)
          .setUsers(0)
          .build();
        Log.d(TAG, "createGroup: " + currentGroup.toString());
    }

    public void pushGroup() {
        repository.pushGroup(currentGroup);
    }
}