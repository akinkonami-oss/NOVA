package com.nova.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(7, 10, 18);
    private final int panel = Color.rgb(17, 23, 36);
    private final int blue = Color.rgb(83, 155, 255);
    private LinearLayout root, appGrid;
    private EditText search;
    private final List<AppEntry> apps = new ArrayList<>();

    static class AppEntry {
        String label, pkg;
        android.graphics.drawable.Drawable icon;
        AppEntry(String l, String p, android.graphics.drawable.Drawable i) { label=l; pkg=p; icon=i; }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildHome();
        loadApps();
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius); return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    private void buildHome() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(38), dp(22), dp(14)); root.setBackgroundColor(bg);
        setContentView(root);

        TextView brand = text("N O V A", 13, blue, true);
        root.addView(brand, new LinearLayout.LayoutParams(-1, dp(28)));

        TextView clock = text(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()), 54, Color.WHITE, false);
        clock.setGravity(Gravity.CENTER); clock.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        root.addView(clock, new LinearLayout.LayoutParams(-1, dp(82)));
        TextView date = text(new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new Date()), 14, 0xff9ba8bd, false);
        date.setGravity(Gravity.CENTER);
        root.addView(date, new LinearLayout.LayoutParams(-1, dp(30)));

        View line = new View(this); line.setBackgroundColor(0xff263249);
        LinearLayout.LayoutParams lpLine = new LinearLayout.LayoutParams(-1, dp(1)); lpLine.setMargins(0,dp(18),0,dp(18)); root.addView(line,lpLine);

        TextView heading = text("Your space", 20, Color.WHITE, true);
        root.addView(heading, new LinearLayout.LayoutParams(-1, dp(34)));
        TextView sub = text("Everything you need, one tap away.", 13, 0xff8290a6, false);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(26)));

        search = new EditText(this);
        search.setSingleLine(true); search.setTextColor(Color.WHITE); search.setHintTextColor(0xff7f8ba0);
        search.setHint("Search your apps"); search.setTextSize(15);
        search.setPadding(dp(16),0,dp(16),0); search.setBackground(rounded(panel, dp(16)));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(54)); slp.setMargins(0,dp(18),0,dp(14)); root.addView(search,slp);

        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        appGrid = new LinearLayout(this); appGrid.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(appGrid); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        TextView footer = text("NOVA  •  MINIMAL BY DESIGN", 10, 0xff59677d, true);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer,new LinearLayout.LayoutParams(-1,dp(28)));

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a) {}
            public void onTextChanged(CharSequence s,int st,int before,int count) { renderApps(s.toString()); }
            public void afterTextChanged(Editable e) {}
        });
    }

    private void loadApps() {
        apps.clear();
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        for (android.content.pm.ResolveInfo ri : pm.queryIntentActivities(intent, 0)) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            try {
                apps.add(new AppEntry(ri.loadLabel(pm).toString(), pkg, ri.loadIcon(pm)));
            } catch (Exception ignored) {}
        }
        Collections.sort(apps, (a,b) -> a.label.compareToIgnoreCase(b.label));
        renderApps("");
    }

    private void renderApps(String filter) {
        if (appGrid == null) return;
        appGrid.removeAllViews();
        String q = filter.toLowerCase(Locale.getDefault()).trim();
        int shown = 0;
        for (AppEntry app : apps) {
            if (!app.label.toLowerCase(Locale.getDefault()).contains(q)) continue;
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(12),dp(8),dp(12),dp(8));
            row.setBackground(rounded(panel,dp(14)));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1,dp(62));
            rp.setMargins(0,0,0,dp(8)); appGrid.addView(row,rp);

            android.widget.ImageView icon = new android.widget.ImageView(this);
            icon.setImageDrawable(app.icon);
            row.addView(icon,new LinearLayout.LayoutParams(dp(36),dp(36)));
            TextView name = text(app.label,15,Color.WHITE,true);
            LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(0,-1,1);
            np.setMargins(dp(14),0,0,0); row.addView(name,np);
            TextView arrow = text("›",26,blue,false); arrow.setGravity(Gravity.CENTER);
            row.addView(arrow,new LinearLayout.LayoutParams(dp(24),-1));
            row.setOnClickListener(v -> {
                Intent launch = getPackageManager().getLaunchIntentForPackage(app.pkg);
                if (launch != null) startActivity(launch);
            });
            shown++;
        }
        if (shown == 0) {
            TextView empty = text("No apps found",14,0xff8290a6,false);
            empty.setGravity(Gravity.CENTER); appGrid.addView(empty,new LinearLayout.LayoutParams(-1,dp(70)));
        }
    }

    @Override protected void onResume() { super.onResume(); if (root != null) loadApps(); }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
}
