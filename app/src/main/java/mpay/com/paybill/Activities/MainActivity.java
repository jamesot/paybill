package mpay.com.paybill.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardGridView;
import mpay.com.paybill.Data.PaybillWrapper;
import mpay.com.paybill.Model.Favorite;
import mpay.com.paybill.Model.MyShortcuts;
import mpay.com.paybill.Model.Paybill;
import mpay.com.paybill.Model.Post;
import mpay.com.paybill.Model.Suggestion;
import mpay.com.paybill.R;

import static mpay.com.paybill.Model.MyShortcuts.checkDefaults;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    static CardGridArrayAdapter mCardArrayAdapter;

    @Bind(R.id.floating_search_view)
    FloatingSearchView mSearchView;
    private String mLastQuery;
    LinearLayout searchContainer;
    EditText toolbarSearchView;
    ImageView searchClearButton;
    private boolean mIsDarkSearchTheme = false;
    private boolean search = false;
    private DrawerLayout drawer;
    protected JSONObject query = null;
    static VideoView vv;
    private SliderLayout mDemoSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkDefaults("login", getBaseContext())) {
            MyShortcuts.setDefaults("login", "false", getBaseContext());
        }
        setContentView(R.layout.activity_home_page);
        ListHistory("paybill", "true");

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("1", MyShortcuts.baseURL()+"images/advertis.jpg");
        url_maps.put("2", MyShortcuts.baseURL()+"images/advertis1.jpg");
        url_maps.put("3", MyShortcuts.baseURL()+"images/advertis2.jpg");
        url_maps.put("4", MyShortcuts.baseURL()+"images/advertis3.jpg");
        url_maps.put("5", MyShortcuts.baseURL()+"images/advertis4.jpg");
        url_maps.put("6", MyShortcuts.baseURL()+"images/advertis5.jpg");

       /* HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("advertisement 1 ", R.drawable.five);
        file_maps.put("advertisement 2", R.drawable.four);
        file_maps.put("advertisement 3", R.drawable.home);*/


        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
      /*  LinearLayout linearLayout = (LinearLayout)findViewById(R.id.llayout);
        TextView textView = new TextView(this);
        textView.setText("Search your pay bill's above.");
        linearLayout.addView(textView);*/


       /* if (MyShortcuts.getDefaults("login", getBaseContext()).equals("true")) {
//            TODO original set content view
            setContentView(R.layout.activity_home_page);
        } else {
            setContentView(R.layout.activity_home_page);
        }*/

        ButterKnife.bind(this);
//        ca-app-pub-7696018058422540~3532583317
        MobileAds.initialize(this, "ca-app-pub-2431263302089833~7164561708");
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        setTitle("Paybill");

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
       /* JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.video);
        jcVideoPlayerStandard.setVisibility(View.VISIBLE);
        jcVideoPlayerStandard.getLayoutParams().height = 300;
        jcVideoPlayerStandard.setUp("http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4"
                , JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "Advertisement");
        jcVideoPlayerStandard.startPlayLocic();*/

//TODO below is the video code to use to show

// TODO Custom advertisements, Search, Server, Login to work, reduce size of Cards

//        getSqlite("paybill");
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setBackgroundColor(Color.parseColor("#99A62B"));
        mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
        mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
        mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
//        mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));

//        TODO Uncomment once server side is done
        if (MyShortcuts.hasInternetConnected(getBaseContext())) {
//            truncatePaybill();
//            getPaybill();
            OnlinePaybill();
//            getSqlite("paybill");
        } else {
//            getSqlite("paybill");

        }

//        This listener listens to query changes and offer suggestions
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                //get suggestions based on newQuery
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.

                    mSearchView.setBackgroundColor(Color.parseColor("#ffffff"));
                    mSearchView.setViewTextColor(Color.parseColor("#000000"));
                    mSearchView.setHintTextColor(Color.parseColor("#000000"));
                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#000000"));
                    mSearchView.setMenuItemIconColor(Color.parseColor("#000000"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#000000"));
                    mSearchView.setClearBtnColor(Color.parseColor("#000000"));
                    mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#000000"));
                    mSearchView.setSuggestionsTextColor(Color.parseColor("#000000"));
                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    mSearchView.swapSuggestions(getSuggestion("paybill", newQuery));


                    //let the users know that the background
                    //process has completed
                    mSearchView.hideProgress();

                }

                Log.d("Researching", "onSearchTextChanged()");
            }
        });


        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login:
                        Intent intent = new Intent(getBaseContext(), AdminLogin.class);
                        startActivity(intent);
                }
            }


        });


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {


                Suggestion suggestion = (Suggestion) searchSuggestion;
//                List<Suggestion> mDataSet = new ArrayList<>();
//                mDataSet=getSuggestion("paybill", suggestion.getBody());
                mSearchView.setBackgroundColor(Color.parseColor("#99A62B"));
                mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                search = true;


                getSearchList("paybill", suggestion.getBody());
                mDemoSlider.removeAllViewsInLayout();
                mDemoSlider.setVisibility(View.INVISIBLE);



               /* DataHelper.findColors(MainActivity.this, suggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(List<ColorWrapper> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });*/
                Log.d("Suggestion clicked", "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                search = true;
                mLastQuery = query;
                mSearchView.setBackgroundColor(Color.parseColor("#99A62B"));
                mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                mDemoSlider.setVisibility(View.INVISIBLE);

                getSearchList("paybill", query);

               /* DataHelper.findColors(MainActivity.this, query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(List<ColorWrapper> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });*/
                Log.d("Searching suggestion", "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
//TODO get history
                mSearchView.setBackgroundColor(Color.parseColor("#ffffff"));
                mSearchView.setViewTextColor(Color.parseColor("#000000"));
                mSearchView.setHintTextColor(Color.parseColor("#000000"));
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#000000"));
                mSearchView.setMenuItemIconColor(Color.parseColor("#000000"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#000000"));
                mSearchView.setClearBtnColor(Color.parseColor("#000000"));
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#000000"));
                mSearchView.setSuggestionsTextColor(Color.parseColor("#000000"));
                mDemoSlider.setVisibility(View.INVISIBLE);
                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(getHistory("paybill", "true"));

                Log.d("Focusing", "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                /*if (!mLastQuery.equals(null)) {
                    mSearchView.setSearchBarTitle(mLastQuery);
                }*/
                mDemoSlider.setVisibility(View.INVISIBLE);
                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());
                mSearchView.setBackgroundColor(Color.parseColor("#99A62B"));
                mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                Log.d("Focuse cleared", "onFocusCleared()");

            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                Suggestion PaybillSuggestion = (Suggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

                if (PaybillSuggestion.getIsHistory().equals("true")) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = PaybillSuggestion.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                CardGridView listView = (CardGridView) findViewById(R.id.carddemo_grid_base1);

                listView.setTranslationY(newHeight);
                mDemoSlider.setTranslationY(newHeight);

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mSearchView.setLeftMenuOpen(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {

            }
        });


    }


    private void search() {
        Post.getData(MyShortcuts.baseURL() + "/search", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject name = jsonArray.getJSONObject(i);
                        String paybillName = name.getString("name");
                        String PaybillNumber = name.getString("paybillNumber");
                        String Email = name.getString("email");
                        String imageSrc = name.getString("image");


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }


    public class GplayGridCard extends Card {

        protected String mTitle;
        protected TextView mSecondaryTitle;
        protected RatingBar mRatingBar;
        protected int resourceIdThumbnail = -1;
        protected int count;
        protected String url;

        protected String headerTitle;
        protected String secondaryTitle;
        protected float rating;

        public GplayGridCard(Context context) {
            super(context, R.layout.inner_content_detail);
        }


        public GplayGridCard(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        private void init() {

            CardHeader header = new CardHeader(getContext());
            header.setButtonOverflowVisible(true);
            header.setTitle(headerTitle);
            header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
//                    Toast.makeText(getContext(), "Item " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    String selected = card.getId();
                    Log.e("card id is",card.getId());
                    ;
                   /* Toast ToastMessage = Toast.makeText(getApplicationContext(), "No info for " + card.getTitle() + "!", Toast.LENGTH_SHORT);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background_color);
                    ToastMessage.show();
*/
                    final TextView subtitle = (TextView) findViewById(R.id.carddemo_gplay_main_inner_subtitle);
                    if (item.getTitle().equals("Info")) {
                        Intent intent = new Intent(getBaseContext(), PaybillDetail.class);
                        intent.putExtra("id", selected);
                        startActivity(intent);
                    } else {
                        if (verifyFavourite("favorite", getCardHeader().getTitle())) {
                            MyShortcuts.showToast(getCardHeader().getTitle() + " added to my Favorites!", getBaseContext());
                            Favorite favorite = new Favorite();
                            favorite.setName(getCardHeader().getTitle());
                            favorite.setEmail(getCardView().getCard().getId());
                            favorite.setFavorite("true");
                            favorite.setSent("true");
                            favorite.setPaybill_number(subtitle.getText().toString());
                            favorite.save();
                        } else {
                            MyShortcuts.showToast(getCardHeader().getTitle() + " is already on your favorite list", getBaseContext());
                        }
                    }
//                    ID = card.getId();
                }
            });

            addCardHeader(header);
//            Log.e("URL", url);


//            NetworkImageView thumbnail1 =(NetworkImageView) getActivity().findViewById(R.id.card_thumbnail_image);
//            thumbnail1.setImageUrl(url,imageLoader);
//
//            GplayGridThumb thumbnail = new GplayGridThumb(getContext());
//            thumbnail.setUrlResource(url);
//            if (resourceIdThumbnail > -1)
//                thumbnail.setDrawableResource(resourceIdThumbnail);
//            else
//                thumbnail.setDrawableResource(R.drawable.ic_launcher);
//            addCardThumbnail(thumbnail);


          /*  OnCardClickListener clickListener = new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Do something
                }
            };

            addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, clickListener);*/
         /*   setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
//                    Do something
                    String selected= card.getId();
                    Toast.makeText(getBaseContext(), "Item ID is" + selected, Toast.LENGTH_LONG).show();
                   *//* Intent intent =new Intent(getBaseContext(),ProductDetail.class);
                    intent.putExtra("id",selected);
                    intent.putExtra("product_name",card.getTitle());
                    startActivity(intent);*//*
                }
            });*/


        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            TextView title = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_title);
            title.setText(mTitle);

            final TextView subtitle = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle);
            subtitle.setText(secondaryTitle);
//            subtitle.setTextIsSelectable(true);
            subtitle.setClickable(true);
            subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(subtitle.getText());
//                    Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    MyShortcuts.showToast("Copied to clipboard!", getBaseContext());
                /*    Intent intent = new Intent(getContext(), EditPatient.class);
                    intent.putExtra("ID", getId());
                    startActivity(intent);*/
//                    getParentCard().getId();
                    subtitle.getText();
                }
            });

            final LikeButton likeButton = (LikeButton) view.findViewById(R.id.heart);
            likeButton.setVisibility(View.INVISIBLE);


           /* if (!verifyFavourite("favorite", getCardHeader().getTitle())) {
                likeButton.setLiked(true);
            }*/
            /*likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {


                    if (verifyFavourite("favorite", getCardHeader().getTitle())) {
                        MyShortcuts.showToast(getCardHeader().getTitle() + " added to my Favorites!", getBaseContext());
                        Favorite favorite = new Favorite();
                        favorite.setName(getCardHeader().getTitle());
                        favorite.setEmail(getCardView().getCard().getId());
                        favorite.setFavorite("true");
                        favorite.setSent("true");
                        favorite.setPaybill_number(subtitle.getText().toString());
                        favorite.save();
                    } else {
                        MyShortcuts.showToast(getCardHeader().getTitle() + " is already on your favorite list", getBaseContext());
                    }

                }

                @Override
                public void unLiked(LikeButton likeButton) {

                }
            });*/

           /* final TextView subtitle2 = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle2);
//            subtitle2.setText(secondaryTitle);
            subtitle2.setClickable(true);
            subtitle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    getParentCard().getId();
                   *//* Intent intent = new Intent(getContext(), AllPrescriptions.class);
                    intent.putExtra("ID", getId());
                    startActivity(intent);*//*
                }
            });*/
//            NetworkImageView thumbnail =(NetworkImageView)view.findViewById(R.id.card_thumbnail_image);
//            thumbnail.setImageUrl(url,imageLoader);


        }

        class GplayGridThumb extends CardThumbnail {

            public GplayGridThumb(Context context) {
                super(context);
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                //viewImage.getLayoutParams().width = 196;
                //viewImage.getLayoutParams().height = 196;

            }
        }

    }

  /*  Popularizing app to areas in Nairobi with least users
Continued monitoring of data collected/users*/


    private void getPaybill() {

        Post.getData(MyShortcuts.baseURL() + "fetchPaybill.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String success = null;
                try {
                    JSONObject jObj = new JSONObject(response);
//                            res = jObj.getJSONArray("All");
                    //successfully gotten matatu data
//                        String regno = jObj.getString("regno");
                    query = jObj;

                    JSONArray res = jObj.getJSONArray("All");

                    if (res.length() < 1) {
                        MyShortcuts.showToast("No paybill! Check later", getBaseContext());
                    }

                    Log.e("result: ", res.toString());
                    ArrayList<Card> cards = new ArrayList<Card>();

                    // looping through All res
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject c = res.getJSONObject(i);

                        // Storing each json item in variable

                        String name = c.getString("name");
//                    String description = c.getString("description");
                        //                                children1 = c.getJSONArray("children");
                        Log.e("CategoryFragment", name);
//                    Items items = new Items();
////                    items.setTitle(name);
////                    items.setTheID(c.getString("uuid"));
//                    JSONObject a = c.getJSONObject("primaryImage");
//                    String path = "https://www.oneshoppoint.com/images" + a.getString("path");
//                    items.setThumbnailUrl("https://www.oneshoppoint.com/images" + a.getString("path"));
//                    itemsList.add(items);


                        GplayGridCard card = new GplayGridCard(getBaseContext());

                        card.headerTitle = name;
                        card.secondaryTitle = c.getString("paybill_number");
//                    card.id = c.getString("price");
                        card.setId(c.getString("id"));
//                    card.url = path;
                        card.setTitle(c.getString("name"));
                        int id = Integer.parseInt(c.getString("id"));
                        card.init();
                        cards.add(card);

                        if (verifyFavourite("paybill", name)) {
                            Paybill paybill = new Paybill();
                            paybill.setName(name);
                            paybill.setEmail(c.getString("email"));
                            paybill.setPaybill_number(c.getString("paybill_number"));
                            paybill.setSent("false");
                            paybill.setHistory("false");
                            paybill.setId(id);
                            paybill.setType(c.getString("type"));
                            paybill.setDescription(c.getString("description"));
                            paybill.save();
                        }


//
                    }
                    if (res.length() == 0) {
                        Toast.makeText(getBaseContext(), "No paybills ", Toast.LENGTH_LONG).show();
                    }
                    mCardArrayAdapter = new CardGridArrayAdapter(getBaseContext(), cards);

                    CardGridView listView = (CardGridView) findViewById(R.id.myGrid);
                    if (listView != null) {
                        listView.setAdapter(mCardArrayAdapter);
                    }


                } catch (JSONException e) {
                    // JSON error
                    MyShortcuts.showToast("No paybills! Check later", getBaseContext());
                    e.printStackTrace();
//                    Toast.makeText(getBaseContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("JSON ERROR", e.toString());
                }


            }
        });
    }

//    TODO List History Down

    private List<Suggestion> ListHistory(String table_name, String query) {
        ArrayList<Card> cards = new ArrayList<Card>();
        List<Suggestion> stringList = new ArrayList<Suggestion>();
        Cursor cursor = null;
        List<PaybillWrapper> suggestionList = new ArrayList<>();

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " where history='" + query + "'  order by name ASC",
                    null);
            if (cursor.getCount() < 1) {
                Log.e("No history", "No data");
            }
            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String history = cursor.getString(cursor.getColumnIndex("history"));
                    Suggestion suggestion = new Suggestion(name);
                    suggestion.setIsHistory(history);
                    stringList.add(suggestion);

                    String paybill = cursor.getString(cursor.getColumnIndex("paybill_number"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String cardid = cursor.getString(cursor.getColumnIndex("cardid"));
                    long id = cursor.getLong(cursor.getColumnIndex("id"));


                    Log.e("cardid history",cardid);
                    int ci = Integer.parseInt(cardid);

                    if (i != 5) {
                        GplayGridCard card = new GplayGridCard(getBaseContext());

                        card.headerTitle = name;
                        card.secondaryTitle = paybill;
                        card.mTitle = type;
                        card.setId(ci + "");
                        card.setTitle(name);

                        card.init();
                        cards.add(card);


                        Log.e("History Name", name);
                        i++;
                    }



                } while (cursor.moveToNext());
                Log.e("StringList of names", stringList.toString() + stringList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }


            mCardArrayAdapter = new CardGridArrayAdapter(getBaseContext(), cards);

            CardGridView listView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
            if (listView != null) {
                listView.setAdapter(mCardArrayAdapter);
            }

        }

//TODO commenting out the home Ad advert
       /* GplayGridCard2 card = new GplayGridCard2(getBaseContext());
        CardThumbnail thumb = new CardThumbnail(getBaseContext());
//                    String path = Advertisement(query);
        String path = "http://41.204.186.47:8000/images/advertis.jpg";
        card.headerTitle = query;
        card.secondaryTitle = "";
        card.setTitle("");
        thumb.setUrlResource(path);
        Log.e("path", path);
        thumb.setErrorResource(R.drawable.heart_off);
        card.addCardThumbnail(thumb);
        card.init();

        cards.add(card);*/
        mCardArrayAdapter.notifyDataSetChanged();

        return stringList;
    }

    private void getSqlite(String table_name) {
//        setContentView(R.layout.activity_home_page);
        //TODO changed above line
        search = false;
        List<String> stringList = new ArrayList<String>();
        Cursor cursor = null;
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " order by id",
                    null);
            if (cursor.getCount() < 1) {
                MyShortcuts.showToast("No offline paybills. Turn your mobile data to obtain current paybill list", getBaseContext());
            }
            if (cursor.moveToFirst()) {
                do {

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String paybill = cursor.getString(cursor.getColumnIndex("paybill_number"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String cardid = cursor.getString(cursor.getColumnIndex("cardid"));
                    long id = cursor.getLong(cursor.getColumnIndex("id"));


                    int ci = Integer.parseInt(cardid);


                    GplayGridCard card = new GplayGridCard(getBaseContext());

                    card.headerTitle = name;
                    card.secondaryTitle = paybill;
                    card.mTitle = type;
                    card.setId(ci + "");
                    card.setTitle(name);

                    card.init();
                    cards.add(card);

                    Log.e("paybill", paybill + " and id is " + id + "ci is " + ci);


                    stringList.add(name);

                } while (cursor.moveToNext());
                Log.e("StringList", stringList.toString() + stringList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            mCardArrayAdapter = new CardGridArrayAdapter(getBaseContext(), cards);

            CardGridView listView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
            if (listView != null) {
                listView.setAdapter(mCardArrayAdapter);
            }
        }
    }


    private List<Suggestion> getSuggestion(String table_name, String query) {
        List<Suggestion> stringList = new ArrayList<Suggestion>();
        Cursor cursor = null;
        List<PaybillWrapper> suggestionList = new ArrayList<>();

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " where name like '%" + query + "%'  order by name ASC",
                    null);
            if (cursor.getCount() < 1) {
                Log.e("data", "No data");
            }
            if (cursor.moveToFirst()) {
                do {

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String history = cursor.getString(cursor.getColumnIndex("history"));
                    Log.e("name", name);
                    Suggestion suggestion = new Suggestion(name);
                    suggestion.setIsHistory(history);
                    stringList.add(suggestion);

                } while (cursor.moveToNext());
                Log.e("StringList of names", stringList.toString() + stringList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return stringList;
    }


    private List<Suggestion> getHistory(String table_name, String query) {
        ArrayList<Card> cards = new ArrayList<Card>();
        List<Suggestion> stringList = new ArrayList<Suggestion>();
        Cursor cursor = null;
        List<PaybillWrapper> suggestionList = new ArrayList<>();

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " where history='" + query + "'  order by name ASC",
                    null);
            if (cursor.getCount() < 1) {
                Log.e("No history", "No data");
            }
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String history = cursor.getString(cursor.getColumnIndex("history"));
                    Suggestion suggestion = new Suggestion(name);
                    suggestion.setIsHistory(history);
                    stringList.add(suggestion);

                    Log.e("History Name", name);

                } while (cursor.moveToNext());
                Log.e("StringList of names", stringList.toString() + stringList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return stringList;
    }


    private void getSearchList(String table_name, String query) {

//        setContentView(R.layout.app_bar_detail);
        //TODO changed above line
        setTitle("Search Results");
        List<Suggestion> stringList = new ArrayList<Suggestion>();
        Cursor cursor = null;
        List<PaybillWrapper> suggestionList = new ArrayList<>();
        ArrayList<Card> SearchCards = new ArrayList<Card>();
        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " where name like '" + query + "'  order by name ASC",
                    null);
            if (cursor.getCount() < 1) {
                Log.e("data search list", "No data");
//                MyShortcuts.showToast("No data", getBaseContext());
            }
            if (cursor.moveToFirst()) {
                do {
                    Paybill _paybill = new Paybill();
                    _paybill.setHistory("true");
                    _paybill.updateAll("name=?", query);
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.e("name updated", name);
                    String paybill = cursor.getString(cursor.getColumnIndex("paybill_number"));
                    String cardid = cursor.getString(cursor.getColumnIndex("cardid"));

                    int ci = Integer.parseInt(cardid);
//                    long id = cursor.getLong(cursor.getColumnIndex("cardid"));

                    Log.e("cardsearchlistid",ci+"");
                    GplayGridCard card = new GplayGridCard(getBaseContext());

                    card.headerTitle = name;
                    card.secondaryTitle = paybill;
                    card.setId(ci + "");
                    card.setTitle(name);

                    card.init();
                    SearchCards.add(card);

//                    PaybillWrapper paybillWrapper = new PaybillWrapper(name,);
                    /*Suggestion suggestion = new Suggestion(name);
                    stringList.add(suggestion);*/

                } while (cursor.moveToNext());
//                Log.e("StringList of names", stringList.toString() + stringList.size());

           //TODO image ad search
                if (MyShortcuts.hasInternetConnected(getBaseContext())) {


                    GplayGridCard2 card = new GplayGridCard2(getBaseContext());
                    CardThumbnail thumb = new CardThumbnail(getBaseContext());
                    String path = Advertisement(query);
                    card.headerTitle = query;
                    card.secondaryTitle = "";
                    card.setTitle("");
                    thumb.setUrlResource(path);
//                    Log.e("path", path);
//                    thumb.setErrorResource(R.drawable.heart_off);
                    card.addCardThumbnail(thumb);
                    card.init();
                    SearchCards.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }






            /*if (!mCardArrayAdapter.isEmpty()){
                mCardArrayAdapter.clear();
                mCardArrayAdapter.notifyDataSetChanged();
            }*/
//TODO changed all mygrid

            mCardArrayAdapter = new CardGridArrayAdapter(getBaseContext(), SearchCards);
            CardGridView listView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
//TODO changed above line my grid base 1

            if (listView != null) {
                /*if(listView.getAdapter().getCount()>1){
                    listView.removeAllViews();
                    listView.setAdapter(null);
                }*/
                listView.setAdapter(mCardArrayAdapter);
                mCardArrayAdapter.notifyDataSetChanged();
            }


        }
//        return suggestionList;
    }


    public static void truncatePaybill() {
        Cursor cursor = null;
        Cursor cursor2 = null;
        Boolean ret = false;

        try {
            cursor = Connector.getDatabase().rawQuery("Delete from paybill",
                    null);
            cursor2 = Connector.getDatabase().rawQuery("DELETE FROM SQLITE_SEQUENCE WHERE name='paybill'",
                    null);
            Log.e("delete row count", cursor.getCount() + " 1 and 2 is  " + cursor2.getCount());
            if (cursor.getCount() < 1) {

                Log.e("data", "No data");
                ret = true;

            } else {
                ret = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }
    }


    private boolean verifyFavourite(String table_name, String query) {
        Cursor cursor = null;
        Boolean ret = false;

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " where name='" + query + "'  order by name ASC",
                    null);
            if (cursor.getCount() < 1) {

                Log.e("data", "No data");
                ret = true;
            } else {
                ret = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ret;
    }

    private void setHistory() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            this.slidingMenu.toggle();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.slidingMenu.toggle();
//                return true;

            case R.id.login:
                Intent intent = new Intent(getBaseContext(), AdminLogin.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchView.setBackgroundColor(Color.parseColor("#99A62B"));
        mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
        mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
        mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (search) {
                search = false;
//                getSqlite("paybill");
                vv = (VideoView) findViewById(R.id.video);
                vv.setVisibility(View.INVISIBLE);

            } else {
                super.onBackPressed();
            }
        } else if (search) {
            search = false;
//            getSqlite("paybill");
            vv = (VideoView) findViewById(R.id.video);
            vv.setVisibility(View.INVISIBLE);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, AdminLogin.class);
            startActivity(intent);
//            finish();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, Favorites.class);
            startActivity(intent);

        } else if (id == R.id.wallet) {
            Intent intent = new Intent(this, Wallet.class);
            startActivity(intent);


        } else if (id == R.id.contactus) {
            Intent intent = new Intent(this, ContactUs.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class GplayGridCard2 extends Card {

        protected TextView mTitle;
        protected TextView mSecondaryTitle;
        protected RatingBar mRatingBar;
        protected int resourceIdThumbnail = -1;
        protected int count;


        protected String headerTitle;
        protected String secondaryTitle;
        protected String id;
        protected String email;
        protected float rating;

        public GplayGridCard2(Context context) {
            super(context, R.layout.advert_detail);
        }


        public GplayGridCard2(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        private void init() {

           /* CardHeader header = new CardHeader(getContext());
            header.setButtonOverflowVisible(true);
//            header.setTitle(headerTitle + "  " + secondaryTitle);

            addCardHeader(header);*/

            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {

                }
            });
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

         /*   TextView title = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_title);
            title.setText("");
*//*
            TextView subtitle = (TextView) view.findViewById(R.id.description);
            subtitle.setText(email);*//*
            TextView id1 = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle);
            id1.setText(email);
//            NetworkImageView thumbnail =(NetworkImageView)view.findViewById(R.id.card_thumbnail_image);
//            thumbnail.setImageUrl(url,imageLoader);
*/

        }

        class GplayGridThumb extends CardThumbnail {

            public GplayGridThumb(Context context) {
                super(context);
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                //viewImage.getLayoutParams().width = 196;
                //viewImage.getLayoutParams().height = 196;

            }
        }

    }

    private String Advertisement(String pname) {
        String path = null;
        String video = null;
        Log.e("pname", pname);

        try {
            JSONObject jObj = query;
            JSONArray res = jObj.getJSONArray("All");
//            Log.e("result: ", res.toString());

            // looping through All res
            for (int i = 0; i < res.length(); i++) {
                JSONObject c = res.getJSONObject(i);
//                Log.e("C is ",c.toString());
                // Storing each json item in variable
                String name = c.getString("name");
                if (name.equals(pname)) {

                    video = c.getString("video");

                    if (video.length() > 1) {
//                        path=video;
                        vv = (VideoView) findViewById(R.id.video);
                        vv.setVisibility(View.VISIBLE);
                        vv.getLayoutParams().height = 500;
                        vv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        //Video Loop http://41.204.186.107:8000/images/VID-20160901-WA0003.mp4


                        Uri uri = Uri.parse(video);

                        vv.setVideoURI(uri);
                        vv.requestFocus();

                        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                vv.start();
                                mp.setLooping(true);
                            }
                        });

                    } else {
                        path = c.getString("image");
                    }
                    Log.e("url is", path);
                }


            }


        } catch (JSONException e) {
            // JSON error
            MyShortcuts.showToast("No advertisement", getBaseContext());
            e.printStackTrace();
            Log.e("JSON ERROR", e.toString());
        }

        return path;
    }


    private String HomeAd(String pname) {
        String path = null;
        String video = null;
        Log.e("pname", pname);

        try {
            JSONObject jObj = query;
            JSONArray res = jObj.getJSONArray("All");
//            Log.e("result: ", res.toString());

            // looping through All res
            for (int i = 0; i < res.length(); i++) {
                JSONObject c = res.getJSONObject(i);
//                Log.e("C is ",c.toString());
                // Storing each json item in variable
                String name = c.getString("name");
                if (name.equals(pname)) {

                    video = c.getString("video");

                    if (video.length() > 1) {
//                        path=video;
                        vv = (VideoView) findViewById(R.id.video);
                        vv.setVisibility(View.VISIBLE);
                        vv.getLayoutParams().height = 500;
                        vv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        //Video Loop http://41.204.186.107:8000/images/VID-20160901-WA0003.mp4


                        Uri uri = Uri.parse(video);

                        vv.setVideoURI(uri);
                        vv.requestFocus();

                        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                vv.start();
                                mp.setLooping(true);
                            }
                        });

                    } else {
                        path = c.getString("image");
                    }
                    Log.e("url is", path);
                }


            }


        } catch (JSONException e) {
            // JSON error
            MyShortcuts.showToast("No advertisement", getBaseContext());
            e.printStackTrace();
            Log.e("JSON ERROR", e.toString());
        }

        return path;
    }

    private void OnlinePaybill() {
        Log.e("online", "online");
        Post.getData(MyShortcuts.baseURL() + "fetchPaybill.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String success = null;
                try {
//                    Log.e("result string", response);
                    JSONObject jObj = new JSONObject(response);
//                            res = jObj.getJSONArray("All");
                    //successfully gotten matatu data
//                        String regno = jObj.getString("regno");
                    query = jObj;

                    JSONArray res = jObj.getJSONArray("All");


                    if (res.length() < 1) {
                        MyShortcuts.showToast("No paybill! Check later", getBaseContext());
                    }else{
                        truncatePaybill();
                        Log.e("truncating the paybill","truncated");
                    }

//                    Log.e("result: ", res.toString());


                    // looping through All res
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject c = res.getJSONObject(i);

                        // Storing each json item in variable

                        String name = c.getString("name");
                        String description = c.getString("description");
                        //                                children1 = c.getJSONArray("children");
//                        Log.e("CategoryFragment", name);
//                    Items items = new Items();
////                    items.setTitle(name);
////                    items.setTheID(c.getString("uuid"));
//                    JSONObject a = c.getJSONObject("primaryImage");
//                    String path = "https://www.oneshoppoint.com/images" + a.getString("path");
//                    items.setThumbnailUrl("https://www.oneshoppoint.com/images" + a.getString("path"));
//                    itemsList.add(items);


                        int id = Integer.parseInt(c.getString("id"));
                        ;

                        if (verifyFavourite("paybill", name)) {
//                            Log.e("new paybill", "new paybill");
                            Paybill paybill = new Paybill();
                            paybill.setName(name);
                            paybill.setEmail(c.getString("email"));
                            paybill.setPaybill_number(c.getString("paybill_number"));
                            paybill.setSent("false");
                            paybill.setHistory("false");
                            paybill.setId(id);
                            paybill.setCardid(id + "");
                            paybill.setDescription(description);
                            paybill.setType(c.getString("type"));
                            Log.e("id online saved", id + "");
                            paybill.save();
                        }


//
                    }
//TODO CANCELLED CALLING GETSQLITE FROM HERE
//                    getSqlite("paybill");
                    if (res.length() == 0) {
                        Toast.makeText(getBaseContext(), "No paybills ", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    // JSON error
                    MyShortcuts.showToast("No paybills! Check later", getBaseContext());
                    e.printStackTrace();
//                    Toast.makeText(getBaseContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("JSON ERROR", e.toString());
                }


            }

        });

    }


}
