package mpay.com.paybill.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardGridView;
import mpay.com.paybill.Model.Favorite;
import mpay.com.paybill.Model.MyShortcuts;
import mpay.com.paybill.R;

public class Favorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {
    static CardGridArrayAdapter mCardArrayAdapter;
    protected ArrayList<Card> cards = new ArrayList<Card>();
    /* @Bind(R.id.floating_search_view)
     FloatingSearchView mSearchView;*/
    private String mLastQuery;
    /*LinearLayout searchContainer;
    EditText toolbarSearchView;
    ImageView searchClearButton;
    */
    private SliderLayout mDemoSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        setTitle("Favorite");
        getSqlite("favorite");
        FloatingSearchView mSearchView;
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




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    public void onSliderClick(BaseSliderView slider) {

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

                    Toast ToastMessage = Toast.makeText(getApplicationContext(), "No info for " + card.getTitle() + "!", Toast.LENGTH_SHORT);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background_color);
                    ToastMessage.show();
                }
            });
            addCardHeader(header);
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
                    ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText( subtitle.getText());
//                    Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    MyShortcuts.showToast( "Copied to clipboard!",getBaseContext());
                    subtitle.getText();
                }
            });

            final LikeButton likeButton = (LikeButton) view.findViewById(R.id.heart);
            likeButton.setLiked(true);
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    MyShortcuts.showToast(getCardHeader().getTitle() + " added to my Favorites!", getBaseContext());
                    Favorite favorite = new Favorite();
                    favorite.setName(getCardHeader().getTitle());
                    favorite.setEmail(getCardView().getCard().getId());
                    favorite.setFavorite("true");
                    favorite.setSent("true");
                    favorite.setPaybill_number(subtitle.getText().toString());
                    favorite.save();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
//                    TODO implement Context menu to verify if the user really wants to unlike
                }
            });
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


    private void getSqlite(String table_name) {
        List<String> stringList = new ArrayList<String>();
        Cursor cursor = null;

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " order by id",
                    null);
            if (cursor.getCount() < 1) {
                MyShortcuts.showToast("You don't have any favorite paybill. Once you add a favoirte it would appear here", getBaseContext());
            }

            mDemoSlider.setVisibility(View.INVISIBLE);
            if (cursor.moveToFirst()) {
                do {



                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String paybill = cursor.getString(cursor.getColumnIndex("paybill_number"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    GplayGridCard card = new GplayGridCard(getBaseContext());

                    card.headerTitle = name;
                    card.secondaryTitle = paybill;
                    card.mTitle=type;
                    card.setId(email);
                    card.setTitle(name);

                    card.init();
                    cards.add(card);

                    Log.e("paybill", paybill);


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
                Intent intent = new Intent(getBaseContext(), SavePaybill.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        }else if (id == R.id.contactus) {
            Intent intent = new Intent(this, ContactUs.class);
            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
