package com.mapzen.open.login;

import com.mapzen.open.MapzenApplication;
import com.mapzen.open.R;
import com.mapzen.android.lost.LocationClient;
import com.mapzen.open.activity.BaseActivity;

import com.viewpagerindicator.CirclePageIndicator;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements LoginAdapter.LoginListener {
    public static final String OSM_VERIFIER_KEY = "oauth_verifier";

    @InjectView(R.id.view_pager) ViewPager viewPager;
    @InjectView(R.id.view_pager_indicator) CirclePageIndicator viewPagerIndicator;

    private MapzenApplication app;
    private Token requestToken = null;
    private Verifier verifier;

    @Inject LocationClient locationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MapzenApplication) getApplication();
        app.inject(this);
        setContentView(R.layout.login_activity);
        View rootView = getWindow().getDecorView().getRootView();
        ButterKnife.inject(this, rootView);
        initViewPager();
    }

    private void initViewPager() {
        final LoginAdapter loginAdapter = new LoginAdapter(this);
        loginAdapter.setLoginListener(this);
        viewPager.setAdapter(loginAdapter);
        viewPagerIndicator.setViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationClient.disconnect();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) {
            setAccessToken(intent);
            startBaseActivity();
        }
    }

    public void loginRoutine() {
        (new AsyncTask<Void, Void, Token>() {
            @Override
            protected Token doInBackground(Void... params) {
                try {
                    setRequestToken(app.getOsmOauthService().getRequestToken());
                    return requestToken;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Token url) {
                if (url != null) {
                    openLoginPage(url);
                } else {
                    unableToLogInAction();
                }
            }
        }).execute();
    }

    protected void openLoginPage(Token url) {
        String authenticationUrl = app.getOsmOauthService().getAuthorizationUrl(url);
        Intent oauthIntent = new Intent(Intent.ACTION_VIEW);
        oauthIntent.setData(Uri.parse(authenticationUrl));
        oauthIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(oauthIntent);
    }

    private void startBaseActivity() {
        Intent baseActivity = new Intent(this, BaseActivity.class);
        baseActivity.setData(getIntent().getData());
        startActivity(baseActivity);
        finish();
    }

    private void setAccessToken(Intent intent) {
        Uri uri = intent.getData();
        verifier = new Verifier(uri.getQueryParameter(OSM_VERIFIER_KEY));
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    app.setAccessToken(app.getOsmOauthService()
                            .getAccessToken(requestToken, verifier));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Unable to log in", Toast.LENGTH_LONG).show();
                }

                return null;
            }
        }).execute();
    }

    public void setRequestToken(Token token) {
        requestToken = token;
    }

    protected void unableToLogInAction() {
        Toast.makeText(getApplicationContext(), getString(R.string.login_error),
                Toast.LENGTH_LONG).show();
        startBaseActivity();
    }

    @Override
    public void doLogin() {
        loginRoutine();
    }
}
