package com.emanuelef.remote_capture.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.emanuelef.remote_capture.Billing;

import java.io.Serializable;

public class CaptureSettings implements Serializable {
    public Prefs.DumpMode dump_mode;
    public String app_filter;
    public String collector_address;
    public int collector_port;
    public int http_server_port;
    public boolean socks5_enabled;
    public boolean tls_decryption;
    public String socks5_proxy_address;
    public int socks5_proxy_port;
    public String socks5_username;
    public String socks5_password;
    public Prefs.IpMode ip_mode;
    public boolean root_capture;
    public boolean pcapdroid_trailer;
    public boolean full_payload;
    public boolean block_quic;
    public boolean auto_block_private_dns;
    public boolean pcapng_format;
    public String capture_interface;
    public String pcap_uri = "";
    public String pcap_name = "";
    public int snaplen = 0;
    public int max_pkts_per_flow = 0;
    public int max_dump_size = 0;
    public String mitmproxy_opts;

    public CaptureSettings(Context ctx, SharedPreferences prefs) {
        dump_mode = Prefs.DumpMode.PCAP_FILE;
        app_filter = Prefs.getAppFilter(prefs);
        collector_address = Prefs.getCollectorIp(prefs);
        collector_port = Prefs.getCollectorPort(prefs);
        http_server_port = Prefs.getHttpServerPort(prefs);
        socks5_enabled = Prefs.getSocks5Enabled(prefs);
        socks5_proxy_address = Prefs.getSocks5ProxyAddress(prefs);
        socks5_proxy_port = Prefs.getSocks5ProxyPort(prefs);
        socks5_username = Prefs.isSocks5AuthEnabled(prefs) ? Prefs.getSocks5Username(prefs) : "";
        socks5_password = Prefs.isSocks5AuthEnabled(prefs) ? Prefs.getSocks5Password(prefs) : "";
        ip_mode = Prefs.getIPMode(prefs);
        root_capture = Prefs.isRootCaptureEnabled(prefs);
        pcapdroid_trailer = Prefs.isPcapdroidTrailerEnabled(prefs);
        capture_interface = Prefs.getCaptureInterface(prefs);
        tls_decryption = Prefs.getTlsDecryptionEnabled(prefs);
        full_payload = Prefs.getFullPayloadMode(prefs);
        block_quic = Prefs.blockQuic(prefs);
        auto_block_private_dns = Prefs.isPrivateDnsBlockingEnabled(prefs);
        mitmproxy_opts = Prefs.getMitmproxyOpts(prefs);
        pcapng_format = Prefs.isPcapngEnabled(ctx, prefs);
    }

    public CaptureSettings(Context ctx, Intent intent) {
        dump_mode = Prefs.getDumpMode(getString(intent, "pcap_dump_mode", "none"));
        app_filter = getString(intent, Prefs.PREF_APP_FILTER, "");
        collector_address = getString(intent, Prefs.PREF_COLLECTOR_IP_KEY, "127.0.0.1");
        collector_port = getInt(intent, Prefs.PREF_COLLECTOR_PORT_KEY, 1234);
        http_server_port = getInt(intent, Prefs.PREF_HTTP_SERVER_PORT, 8080);
        socks5_enabled = getBool(intent, Prefs.PREF_SOCKS5_ENABLED_KEY, false);
        socks5_proxy_address = getString(intent, Prefs.PREF_SOCKS5_PROXY_IP_KEY, "0.0.0.0");
        socks5_proxy_port = getInt(intent, Prefs.PREF_SOCKS5_PROXY_PORT_KEY, 8080);
        socks5_username = getString(intent, Prefs.PREF_SOCKS5_USERNAME_KEY, "");
        socks5_password = getString(intent, Prefs.PREF_SOCKS5_PASSWORD_KEY, "");
        ip_mode = Prefs.getIPMode(getString(intent, Prefs.PREF_IP_MODE, Prefs.IP_MODE_DEFAULT));
        root_capture = getBool(intent, Prefs.PREF_ROOT_CAPTURE, false);
        pcapdroid_trailer = getBool(intent, Prefs.PREF_PCAPDROID_TRAILER, false);
        capture_interface = getString(intent, Prefs.PREF_CAPTURE_INTERFACE, "@inet");
        pcap_uri = getString(intent, "pcap_uri", "");
        pcap_name = getString(intent, "pcap_name", "");
        snaplen = getInt(intent, Prefs.PREF_SNAPLEN, 0);
        max_pkts_per_flow = getInt(intent, Prefs.PREF_MAX_PKTS_PER_FLOW, 0);
        max_dump_size = getInt(intent, Prefs.PREF_MAX_DUMP_SIZE, 0);
        tls_decryption = getBool(intent, Prefs.PREF_TLS_DECRYPTION_KEY, false);
        full_payload = false;
        block_quic = getBool(intent, Prefs.PREF_BLOCK_QUIC, false);
        auto_block_private_dns = getBool(intent, Prefs.PREF_AUTO_BLOCK_PRIVATE_DNS, true);
        mitmproxy_opts = getString(intent, Prefs.PREF_MITMPROXY_OPTS, "");
        pcapng_format = getBool(intent, Prefs.PREF_PCAPNG_ENABLED, false) && Billing.newInstance(ctx).isPurchased(Billing.PCAPNG_SKU);
    }

    private static String getString(Intent intent, String key, String def_value) {
        String val = intent.getStringExtra(key);
        return (val != null) ? val : def_value;
    }

    // get a integer value from the bundle. The value may be represented as an int or as a string.
    private static int getInt(Intent intent, String key, int def_value) {
        Bundle bundle = intent.getExtras();

        String s = bundle.getString(key);
        if(s != null)
            return Integer.parseInt(s);
        return bundle.getInt(key, def_value);
    }

    // get a boolean value from the bundle. The value may be represented as a bool or as a string.
    private static boolean getBool(Intent intent, String key, boolean def_value) {
        Bundle bundle = intent.getExtras();

        String s = bundle.getString(key);
        if(s != null)
            return Boolean.parseBoolean(s);
        return bundle.getBoolean(key, def_value);
    }
}
