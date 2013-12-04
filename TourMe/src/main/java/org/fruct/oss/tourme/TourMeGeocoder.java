package org.fruct.oss.tourme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Geocoder with necessary functions
 * (uses standard Android geocoder)
 */
public class TourMeGeocoder {
    Geocoder geocoder;
    Address address;

    public TourMeGeocoder(Context context, double latitude, double longitude) {
        geocoder = new Geocoder(context); // Will use current device locate

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 2);
            address = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get cointry name (e.g. 'Finland')
    public String getCountry() {
        return address.getCountryName();
    }

    // Get region (e.g. 'South Karelia')
    public String getRegion() {
        return address.getAdminArea();
    }

    // Get city name ('e.g. Imatra')
    public String getCity() {
        return address.getLocality();
    }

    // Get currency for country user are
    public String getCurrency() {
        Locale locale = address.getLocale();
        Currency currency = Currency.getInstance(locale);

        Log.e("curr", address.getCountryName());
        return currency.getCurrencyCode();
    }
}
