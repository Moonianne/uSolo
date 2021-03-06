package com.android.group.model.foursquare;

import java.util.List;

public class VenueResponse {
    private List<Venue> venues;

    public VenueResponse(final List<Venue> venues) {
        this.venues = venues;
    }

    public List<Venue> getVenues() {
        return venues;
    }
}
