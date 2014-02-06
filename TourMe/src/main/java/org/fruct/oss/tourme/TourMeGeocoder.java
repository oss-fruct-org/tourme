package org.fruct.oss.tourme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Geocoder with necessary functions
 * (uses standard Android geocoder)
 */
public class TourMeGeocoder {
    Geocoder geocoder;
    Address address;

    public static final Map<String, String> currencies = new HashMap<String, String>(){{
        put("AF", "AFN");
        put("AG", "XCD");
        put("AD", "EUR");
        put("AE", "AED");
        put("IM", "IMP");
        put("AN", "ANG");
        put("AO", "AOA");
        put("AL", "ALL");
        put("AM", "AMD");
        put("TW", "TWD");
        put("AI", "XCD");
        put("AW", "AWG");
        put("AT", "EUR");
        put("AU", "AUD");
        put("AR", "ARS");
        put("AS", "USD");
        put("TL", "IDR");
        put("AZ", "AZN");
        put("IL", "ILS");
        put("JP", "JPY");
        put("SN", "XOF");
        put("WS", "USD");
        put("JO", "JOD");
        put("TO", "TOP");
        put("JM", "JMD");
        put("QA", "QAR");
        put("SH", "SHP");
        put("YT", "EUR");
        put("LI", "CHF");
        put("JE", "JEP");
        put("SI", "EUR");
        put("FJ", "USD");
        put("LV", "LVL");
        put("LY", "LYD");
        put("TZ", "TZS");
        put("PT", "EUR");
        put("PW", "USD");
        put("PR", "USD");
        put("TV", "TVD");
        put("IT", "EUR");
        put("PY", "PYG");
        put("IS", "ISK");
        put("IQ", "IQD");
        put("PE", "PEN");
        put("IO", "USD");
        put("PG", "PGK");
        put("PF", "XPF");
        put("PA", "PAB");
        put("LK", "LKR");
        put("PM", "EUR");
        put("PL", "PLN");
        put("BB", "BBD");
        put("PN", "NZD");
        put("PH", "PHP");
        put("PK", "PKR");
        put("CL", "CLP");
        put("CM", "XAF");
        put("CN", "CNY");
        put("CO", "COP");
        put("CH", "CHF");
        put("CI", "XOF");
        put("CK", "NZD");
        put("IR", "IRR");
        put("CF", "XAF");
        put("CA", "CAD");
        put("CC", "AUD");
        put("CX", "AUD");
        put("CY", "EUR");
        put("CZ", "CZK");
        put("CU", "CUP");
        put("CV", "CVE");
        put("MM", "MNK");
        put("CR", "CRC");
        put("LA", "LAK");
        put("IN", "INR");
        put("LC", "XCD");
        put("LB", "LBP");
        put("SX", "ANG");
        put("SY", "SYP");
        put("SZ", "SZL");
        put("TH", "THB");
        put("ST", "STD");
        put("SV", "USD");
        put("TK", "NZD");
        put("SR", "SRD");
        put("SS", "SSP");
        put("SL", "SLL");
        put("SM", "EUR");
        put("LS", "LSL");
        put("LR", "LRD");
        put("LU", "EUR");
        put("LT", "LTL");
        put("SJ", "NOK");
        put("SK", "EUR");
        put("SD", "SDG");
        put("SE", "SEK");
        put("SG", "SGD");
        put("SA", "SAR");
        put("SB", "SBD");
        put("SC", "SCR");
        put("NO", "NOK");
        put("BA", "BAM");
        put("GY", "GYD");
        put("BG", "BGN");
        put("BF", "XOF");
        put("BE", "EUR");
        put("BD", "BDT");
        put("BJ", "XOF");
        put("BI", "BIF");
        put("BH", "BHD");
        put("BO", "BOB");
        put("BN", "BND");
        put("BM", "BMD");
        put("BS", "BSD");
        put("BR", "BRL");
        put("BQ", "USD");
        put("UY", "UYU");
        put("BW", "BWP");
        put("BV", "NOK");
        put("BT", "BTN");
        put("BZ", "BZD");
        put("BY", "BYR");
        put("TC", "USD");
        put("RS", "RSD");
        put("KE", "KES");
        put("KG", "KGS");
        put("RW", "RWF");
        put("RU", "RUB");
        put("KM", "KMF");
        put("ZA", "ZAR");
        put("KH", "KHR");
        put("KI", "AUD");
        put("TD", "XAF");
        put("KW", "KWD");
        put("KP", "KPW");
        put("RE", "EUR");
        put("US", "USD");
        put("RO", "RON");
        put("KY", "KYD");
        put("KZ", "KZT");
        put("EH", "MAD");
        put("YE", "YER");
        put("EC", "USD");
        put("EG", "EGP");
        put("EE", "EUR");
        put("VN", "VND");
        put("ER", "ETB");
        put("ES", "EUR");
        put("ET", "ETB");
        put("UZ", "UZS");
        put("NL", "EUR");
        put("FR", "EUR");
        put("NI", "NIO");
        put("TT", "TTD");
        put("NG", "NGN");
        put("NF", "AUD");
        put("NE", "XOF");
        put("NC", "XPF");
        put("NA", "NAD");
        put("NZ", "NZD");
        put("UM", "USD");
        put("FO", "DKK");
        put("FM", "USD");
        put("UA", "UAH");
        put("FK", "FKP");
        put("NR", "AUD");
        put("FI", "EUR");
        put("NP", "NPR");
        put("VG", "USD");
        put("DK", "DKK");
        put("DJ", "DJF");
        put("DM", "XCD");
        put("SO", "SOS");
        put("DO", "DOP");
        put("DE", "EUR");
        put("VE", "VEF");
        put("TN", "TND");
        put("DZ", "DZD");
        put("VC", "XCD");
        put("OM", "OMR");
        put("MC", "EUR");
        put("MA", "MAD");
        put("MF", "EUR");
        put("MD", "MDL");
        put("ME", "EUR");
        put("MH", "USD");
        put("TR", "TRY");
        put("MN", "MNT");
        put("ML", "XOF");
        put("UG", "UGX");
        put("MR", "MRO");
        put("MS", "XCD");
        put("MP", "USD");
        put("MQ", "EUR");
        put("MV", "MVR");
        put("MW", "MWK");
        put("MT", "EUR");
        put("MU", "MUR");
        put("MZ", "MZN");
        put("MX", "MXN");
        put("MY", "MYR");
        put("KN", "XCD");
        put("TG", "XOF");
        put("TF", "EUR");
        put("CW", "ANG");
        put("ID", "IDR");
        put("WF", "XPF");
        put("HU", "HUF");
        put("HT", "USD");
        put("GB", "GBP");
        put("HR", "HRK");
        put("HM", "AUD");
        put("HN", "HNL");
        put("HK", "HKD");
        put("IE", "EUR");
        put("KR", "KRW");
        put("ZM", "ZMK");
        put("TJ", "TJS");
        put("GP", "EUR");
        put("GR", "EUR");
        put("GT", "GTQ");
        put("GU", "USD");
        put("VA", "EUR");
        put("GW", "XOF");
        put("ZW", "ZWD");
        put("VI", "USD");
        put("GA", "XAF");
        put("GD", "XCD");
        put("GE", "GEL");
        put("GF", "EUR");
        put("GG", "GBP");
        put("GH", "GHS");
        put("GI", "GIP");
        put("GL", "DKK");
        put("GM", "GMD");
        put("GN", "GNF");
    }};;

    public TourMeGeocoder(Context context, double latitude, double longitude) {
        geocoder = new Geocoder(context); // Will use current device locate

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 2);
            address = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
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
        return currencies.get(address.getCountryCode());
    }

    /**
     * Get device currency
     * @param context
     * @return currency code
     */
    public String getDeviceCurrency() {
        Currency currency = Currency.getInstance(Locale.getDefault());

        return currency.getCurrencyCode();
    }
}
