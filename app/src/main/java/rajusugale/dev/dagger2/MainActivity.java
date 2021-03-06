package rajusugale.dev.dagger2;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rajusugale.dev.dagger2.models.Repository;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Inject  //Uncommenting This line will not Inject DI.. Please Try this case..
    SharedPreferences mSharedPreferences;//Sit back relax

    @Inject //DI
    ViewsApiEnd mViewsApiEnd;

    @Inject //DI
    Retrofit mRetrofit;

    @BindView(R.id.tv_launchCounter)
    TextView tv_font;
    @BindView(R.id.response)
    TextView tv_response;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApp) getApplication()).getGitHubComponent().inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        final TextView refresh = (TextView) findViewById(R.id.buttonRefreshh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Snackbar.make(view,"Please Wait.. Checking Views Api..", Snackbar.LENGTH_SHORT)
                        .setAction("Action",null).show();
                if(mViewsApiEnd!=null)
                    tv_response.setText("ViewsApiEnd Dependency Injection Worked!");
                else {
                    tv_response.setText("ViewsApiEnd Dependency Injection NOT WORKING!");
                    return;
                }

                Call<ArrayList<Repository>> call = mViewsApiEnd.getRepository("rajuse");

                call.enqueue(new Callback<ArrayList<Repository>>() {
                    @Override
                    public void onResponse(Response<ArrayList<Repository>> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Log.i("DEBUG", response.body().toString());
                            Snackbar.make(view,"Data retrieved.. Good Job!", Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();

                            String s=updateCounter();
                           ArrayList<Repository> list= response.body();
                            String strlist="";
                            for (int i=0; i<list.size(); i++){
                                strlist=strlist+list.get(i).getFullName()+ "\n";
                            }

                            if(mRetrofit!=null)
                            tv_response.setText(s+"\nRetrofit Dependency Injection Worked!\n\nYou've Created Repos: \n"+strlist);
                            else {
                                tv_response.setText(s+"\n\nRetrofit Dependency Injection NOT WORKING!");
                            }


                        } else {
                            Log.i("ERROR", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        tv_response.setText("Retrofit onFailure got called :(");

                    }
                });
            }

        });

        updateCounter();

    }

    private String updateCounter() {
        if(mSharedPreferences!=null) {
            tv_response.setText("SharedPreferences Dependency Injection Worked!");
        }else {
            tv_response.setText(getString(R.string.sfnotworking));
            tv_font.setText("Oops! Something went wrong..");
            return getString(R.string.sfnotworking);
        }
        int countTemp=mSharedPreferences.getInt("launch_counter",0);
        int counter = countTemp+1;

        SharedPreferences.Editor editor= mSharedPreferences.edit();
        editor.putInt("launch_counter", counter);
        editor.commit();

        tv_font.setText("You've Got "+ counter+" views");

        return "SharedPreferences Dependency Injection Worked!";
    }


}
