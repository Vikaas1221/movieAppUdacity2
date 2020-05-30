package com.example.movieappudacity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.movieappudacity.Adapters.Adapter;
import com.example.movieappudacity.Models.Favourite;
import com.example.movieappudacity.Models.Model;
import com.example.movieappudacity.Networks.Internet;
import com.example.movieappudacity.R;
import com.example.movieappudacity.Viewmodel.Viewmodel;

import java.util.ArrayList;
import java.util.List;

import static com.example.movieappudacity.Adapters.Adapter.adapterPosition;

public class MainActivity extends AppCompatActivity {
    private static final String LIST_STATE_KEY ="liststate" ;
    RecyclerView movieRecyclerview;
    RecyclerView.Adapter adapter;
    public static Context context;
    Viewmodel viewmodel;
    ProgressBar progressBar;
    public static String save = "";
    private static String POPULAR = "popular";
    private static String TOP_RATED = "top_rated";
    private static String FAVOURITES = "favourites";
    private static String savedString = "";
    private static String LISTSTATE = "list_state";
    private int mOffset;
    Parcelable mListState;
    private static final String SCROLL_OFFSET = "SCROLL_OFFSET";
    private int pos;
    GridLayoutManager gm;
    LinearLayoutManager ln;
    ArrayList<Model> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        movieRecyclerview = findViewById(R.id.movieRecycler);
        progressBar = findViewById(R.id.progressbar);
            save = POPULAR;
            Log.d(SCROLL_OFFSET,"in oncreate else part savedinstance"+mOffset);
            fetchMovies(POPULAR);
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mostPopular: {
                progressBar.setVisibility(View.VISIBLE);
                save = POPULAR;
                setTitle(getString(R.string.app_name) + "-Popular Movies");
                fetchMovies(POPULAR);

                break;
            }
            case R.id.topRated: {
                progressBar.setVisibility(View.VISIBLE);
                save = TOP_RATED;
                setTitle(getString(R.string.app_name) + "- Top Rated Movies");
                fetchMovies(TOP_RATED);
                break;
            }
            case R.id.favourites: {

                progressBar.setVisibility(View.VISIBLE);
                save = FAVOURITES;
                setTitle(getString(R.string.app_name) + "- Favourites");
                getFavouriteMovies();
                break;

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchMovies(String query) {
        Internet internet = new Internet();
        if (!internet.checkConnection())
        {

            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            getFavouriteMovies();
            setTitle(getString(R.string.app_name)+"Favourite");
            return;
        }
        movieRecyclerview.setItemAnimator(null);

        gm = new GridLayoutManager(MainActivity.this, 2);
        movieRecyclerview.setLayoutManager(gm);

        movieRecyclerview.setHasFixedSize(true);
        pos = gm.findFirstCompletelyVisibleItemPosition();
        viewmodel = ViewModelProviders.of(this).get(Viewmodel.class);
        viewmodel.setQuery(query);
        viewmodel.getMutableLiveData().observe(this, new Observer<ArrayList<Model>>() {
            @Override
            public void onChanged(ArrayList<Model> models) {
                if (models.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                    return;
                }


                adapter = new Adapter(getApplicationContext(), models);
                movieRecyclerview.setAdapter(adapter);
                movieRecyclerview.setSaveEnabled(true);

                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);


            }

        });

    }

    public void getFavouriteMovies() {

        movieRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        movieRecyclerview.setHasFixedSize(true);
        viewmodel = ViewModelProviders.of(this).get(Viewmodel.class);
        viewmodel.getFavouriteMovies().observe(this, new Observer<List<Favourite>>() {
            @Override
            public void onChanged(List<Favourite> favourites) {

                List<Model> models = new ArrayList<>();
                for (int i = 0; i < favourites.size(); i++) {
                    Model model = new Model(favourites.get(i).getOriginalTitle(), favourites.get(i).getMovieImage()
                            , favourites.get(i).getUserRating(), favourites.get(i).getRelaseDate(), favourites.get(i).getOverView()
                            , favourites.get(i).id());

                    models.add(model);
                }
                adapter = new Adapter(getApplicationContext(), models);
                movieRecyclerview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lkjhg", "in On pause" + save);
        assert save != null;
        if (!save.equals("")) {
            savedString = save;
        }
        Log.d("asqwe", "" + savedString);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("hjkl", "in on resume" + savedString);

        if (savedString.equals(POPULAR))
        {
            fetchMovies(POPULAR);
        } else if (savedString.equals(TOP_RATED))
        {
            fetchMovies(TOP_RATED);
        } else if (savedString.equals(FAVOURITES))
        {
            getFavouriteMovies();
        } else {
            fetchMovies(POPULAR);
            setTitle(getString(R.string.app_name) + "Popular");
        }
        if (mListState != null) {
            gm.onRestoreInstanceState(mListState);
        }

    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        pos=gm.findFirstCompletelyVisibleItemPosition();
//        outState.putInt(LISTSTATE,pos);
////        outState.putParcelable(KEY_INSTSNCE_STATE_BY_POSITION,gm.onSaveInstanceState());
////        Log.d("asvdb","in oncreate2 savedinstance ");
//    }
    protected void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        // Save list state
        mListState = gm.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }
}
