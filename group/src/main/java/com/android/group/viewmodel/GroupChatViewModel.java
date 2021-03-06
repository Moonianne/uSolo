package com.android.group.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.Query;

import org.pursuit.firebasetools.Repository.FireRepo;
import org.pursuit.firebasetools.model.Group;
import org.pursuit.firebasetools.model.Message;

import java.text.DateFormat;
import java.util.Date;

import io.reactivex.disposables.Disposable;

public class GroupChatViewModel extends ViewModel {

    private final FireRepo fireRepo = FireRepo.getInstance();
    private String dBRef;

    private SnapshotParser<Message> parser;
    //TODO: Use FireBaseAuth to give viewmodel UserName.
    private String username = "anonymous";
    private static final String PROFILE_PREFS = "PROFILE";
    private static final String PROFILE_PREFS_PHOTO_URL = "PROFILE_PHOTO_URL";

    public boolean hasText(CharSequence charSequence) {
        return charSequence.toString().trim().length() > 0;
    }

    public Query getMessageDatabaseReference(String ref) {
        //Passing Reference to the chat room
        this.dBRef = ref;
        return fireRepo.getGroupMessageDatabaseReference(dBRef);
    }

    private String getPhotoUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PROFILE_PREFS_PHOTO_URL, "");
    }

    public void pushMessage(String message, Context context) {
        fireRepo.pushGroupChatMessage(dBRef, new Message(System.currentTimeMillis(), username, message, getPhotoUrl(context), fireRepo.getCurrentUser().getUid()));
    }

    public SnapshotParser<Message> getParser() {
        return parser;
    }

    public void parseMessage() {
        parser = dataSnapshot -> {
            Message message = dataSnapshot.getValue(Message.class);
            if (message != null) {
                message.setiD(dataSnapshot.getKey());
            }
            return message;
        };
    }

    @NonNull
    public String convertToReadableTime(@NonNull final Message message) {
        DateFormat formatter = DateFormat.getDateTimeInstance();
        return formatter.format(new Date(message.getTimeStamp()));
    }

    public Disposable removeUserFromGroup(String groupKey) {
        return fireRepo.removeUserFromGroup(groupKey);
    }

}
