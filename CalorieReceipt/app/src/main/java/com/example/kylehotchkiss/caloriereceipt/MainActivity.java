package com.example.kylehotchkiss.caloriereceipt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;


public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    private CardView new_receipt;
    private Spinner activity_choose;
    private TextView units;
    private EditText input;
    private EditText name;
    private Button button2;
    private Button button3;

    String[] activities;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int[] tabIconsActive = {
            R.drawable.ic_poll_white_36dp,
            R.drawable.ic_receipt_white_36dp
    };

    private int[] tabIconsInactive = {
            R.drawable.ic_poll_grey_800_36dp,
            R.drawable.ic_receipt_grey_800_36dp
    };

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        myDb = new DatabaseHelper(this);
        new_receipt = (CardView) findViewById(R.id.new_receipt);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Calculator");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(tabIconsActive[0]);
        tabLayout.getTabAt(1).setIcon(tabIconsInactive[1]);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                switch (position) {
                    case 0:
                        /*
                        setting Home as White and rest grey
                        and like wise for all other positions
                         */
                        tabLayout.getTabAt(0).setIcon(tabIconsActive[0]);
                        tabLayout.getTabAt(1).setIcon(tabIconsInactive[1]);
                        setTitle("Calculator");
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(tabIconsInactive[0]);
                        tabLayout.getTabAt(1).setIcon(tabIconsActive[1]);
                        setTitle("Receipts");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        final String[] activities = {"Select Activity...", "Cycling", "Jogging", "Jumping Jacks", "Leg-Lift", "Plank",
                "Push ups", "Pull ups", "Sit ups", "Squats","Stair-Climbing","Swimming", "Walking"};
        final String[] reps = {activities[6], activities[7], activities[8], activities[9]};
        final Map<String, Integer> conversions = new HashMap<String, Integer>();
        conversions.put(activities[1], 12);
        conversions.put(activities[2], 12);
        conversions.put(activities[3], 10);
        conversions.put(activities[4], 25);
        conversions.put(activities[5], 25);
        conversions.put(activities[6], 350);
        conversions.put(activities[7], 100);
        conversions.put(activities[8], 200);
        conversions.put(activities[9], 225);
        conversions.put(activities[10], 15);
        conversions.put(activities[11], 13);
        conversions.put(activities[12], 20);

        final Spinner spinner = (Spinner) findViewById(R.id.activity_chooser);
        units = (TextView) findViewById(R.id.unit_text);
        input = (EditText) findViewById(R.id.rep_amount);
        name = (EditText) findViewById(R.id.receipt_name);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final List<String> prefered_activities = new ArrayList<String>();
        for (int i = 0; i < activities.length; i++) {
            boolean preferred = preferences.getBoolean(activities[i], true);
            if (preferred) {
                prefered_activities.add(activities[i]);
            }
        }
        button2.setOnClickListener(new View.OnClickListener() {
              public void onClick(View view) {
                  InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                  input.clearFocus();
                  imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                  String activity = spinner.getSelectedItem().toString();
                  if (activity.equals("Select Activity...")) {
                      return;
                  }
                  String amount = input.getText().toString();
                  Integer number;
                  if (amount.equals("")) {
                      number = 0;
                  } else {
                      number = Integer.parseInt(amount);
                  }
                  Integer calories = number * 100 / conversions.get(activity);
                  new_receipt = (CardView) findViewById(R.id.new_receipt);
                  name = (EditText) findViewById(R.id.receipt_name);
                  Integer is_reps;
                  if (activity.equals(reps[0]) || activity.equals(reps[1]) || activity.equals(reps[2]) || activity.equals(reps[3])) {
                      is_reps = 1;
                  } else {
                      is_reps = 0;
                  }
                  myDb.saveItem(name.getText().toString(), activity, number, is_reps, calories);
                  Context context = getApplicationContext();
                  CharSequence text = "Activity Added!";
                  int duration = Toast.LENGTH_LONG;
                  Toast toast = Toast.makeText(context, text, duration);
                  toast.show();
              }
          });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                input.clearFocus();
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                String activity = spinner.getSelectedItem().toString();
                if (activity.equals("Select Activity...")) {
                    return;
                }
                String amount = input.getText().toString();
                Integer number;
                if (amount.equals("")) {
                    number = 0;
                } else {
                    number = Integer.parseInt(amount);
                }
                Integer calories = number * 100 / conversions.get(activity);
                new_receipt = (CardView) findViewById(R.id.new_receipt);
                name = (EditText) findViewById(R.id.receipt_name);
                Integer is_reps;
                if (activity.equals(reps[0]) || activity.equals(reps[1]) || activity.equals(reps[2]) || activity.equals(reps[3])) {
                    is_reps = 1;
                } else {
                    is_reps = 0;
                }
                myDb.saveItem(name.getText().toString(), activity, number, is_reps, calories);
                new_receipt.setVisibility(View.INVISIBLE);
                Context context = getApplicationContext();
                CharSequence text = "Receipt Saved!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item,  prefered_activities);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                final float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (290 * scale + 0.5f);
                switch (spinner.getItemAtPosition(position).toString()) {
                    case ("Select Activity..."):
                        break;
                    case ("Burn Calories"):
                        break;
                    case ("Cycling"):
                    case ("Jogging"):
                    case ("Jumping Jacks"):
                    case ("Leg-Lift"):
                    case ("Plank"):
                    case ("Stair-Climbing"):
                    case ("Walking"):
                    case ("Swimming"):
                        units.setText(R.string.units_2);
                        break;
                    case ("Sit ups"):
                    case ("Push ups"):
                    case ("Pull ups"):
                    case ("Squats"):
                        units.setText(R.string.units_1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              new_receipt = (CardView) findViewById(R.id.new_receipt);
              if(new_receipt.getVisibility()==View.INVISIBLE)
                  new_receipt.setVisibility(View.VISIBLE);
              else if(view.getVisibility()==View.VISIBLE)
                  new_receipt.setVisibility(View.INVISIBLE);
          }
      });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a CalculatorFragment (defined as a static inner class below).
            if (position == 0){
                return CalculatorFragment.newInstance(position + 1);
            } else {
                return ReceiptsFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    public static class ReceiptsFragment extends Fragment {
        private ListView receipts;
        DatabaseHelper myDb;

        public static ReceiptsFragment newInstance(int sectionNumber) {
            ReceiptsFragment fragment = new ReceiptsFragment();
            return fragment;
        }

        public ReceiptsFragment() {
        }


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.receipt_fragment, container, false);
            final String[] activities = {"Select Activity...", "Cycling", "Jogging", "Jumping Jacks", "Leg-Lift", "Plank",
                    "Push ups", "Pull ups", "Sit ups", "Squats","Stair-Climbing","Swimming", "Walking"};
            final String[] reps = {activities[6], activities[7], activities[8], activities[9]};
            final Map<String, Integer> conversions = new HashMap<String, Integer>();
            myDb = new DatabaseHelper(getContext());
            conversions.put(activities[1], 12);
            conversions.put(activities[2], 12);
            conversions.put(activities[3], 10);
            conversions.put(activities[4], 25);
            conversions.put(activities[5], 25);
            conversions.put(activities[6], 350);
            conversions.put(activities[7], 100);
            conversions.put(activities[8], 200);
            conversions.put(activities[9], 225);
            conversions.put(activities[10], 15);
            conversions.put(activities[11], 13);
            conversions.put(activities[12], 20);
            receipts = (ListView) rootView.findViewById(R.id.receipts);
            receipts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> l, View view, int position, long id) {
                    Cursor cursor = (Cursor) ((SimpleCursorAdapter) receipts.getAdapter()).getCursor();
                    cursor.moveToPosition(position);
                    Integer receipt_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REC_COL_2));
                    Cursor items = myDb.getItems((int) id);
                    String message = "";
                    items.moveToFirst();
                    while (!items.isAfterLast()) {
                        message += items.getString(1) + ": " + items.getInt(3);
                        if (items.getInt(2) == 1) {
                            message += " reps, burning ";
                        } else {
                            message += " minutes, burning ";
                        }
                        message += items.getInt(4) + " Calories.\n";
                        items.moveToNext();
                    }
                    message += "Total Calories Burned: " + cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REC_COL_3));
                    String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REC_COL_2));
                    new AlertDialog.Builder(getContext())
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                            .show();
                }
            });
            fillData();
            return rootView;
        }

        public void fillData() {
            Cursor cursor = myDb.getReceipts();
            String[] from = new String[]{DatabaseHelper.REC_COL_2, DatabaseHelper.REC_COL_3};
            int[] to = new int[]{R.id.name, R.id.calories};

            SimpleCursorAdapter cursorAdapter =
                    new SimpleCursorAdapter(getContext(), R.layout.row, cursor, from, to);

            receipts.setAdapter(cursorAdapter);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CalculatorFragment extends Fragment {
        private CardView card;
        private LinearLayout card2;
        private TextView units;
        private RelativeLayout input;
        private Button button;
        private EditText amount;
        private ListView list_results;
        private TextView output;
        private TextView text;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CalculatorFragment newInstance(int sectionNumber) {
            CalculatorFragment fragment = new CalculatorFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public CalculatorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final String[] activities = {"Select Activity...", "Burn Calories", "Cycling", "Jogging", "Jumping Jacks", "Leg-Lift", "Plank",
                                   "Push ups", "Pull ups", "Sit ups", "Squats","Stair-Climbing","Swimming", "Walking"};
            final String[] reps = {activities[6], activities[7], activities[8], activities[9]};
            final Map<String, Integer> conversions = new HashMap<String, Integer>();
            conversions.put(activities[2], 12);
            conversions.put(activities[3], 12);
            conversions.put(activities[4], 10);
            conversions.put(activities[5], 25);
            conversions.put(activities[6], 25);
            conversions.put(activities[7], 350);
            conversions.put(activities[8], 100);
            conversions.put(activities[9], 200);
            conversions.put(activities[10], 225);
            conversions.put(activities[11], 15);
            conversions.put(activities[12], 13);
            conversions.put(activities[13], 20);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
            card = (CardView) rootView.findViewById(R.id.card_view);
            card2 = (LinearLayout) rootView.findViewById(R.id.result_view);
            units = (TextView) rootView.findViewById(R.id.textView3);
            input = (RelativeLayout) rootView.findViewById(R.id.amount);
            amount = (EditText) rootView.findViewById(R.id.editText);
            button = (Button) rootView.findViewById(R.id.button);
            list_results = (ListView) rootView.findViewById(R.id.listView);
            output = (TextView) rootView.findViewById(R.id.result);
            text = (TextView) rootView.findViewById(R.id.textView4);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            final List<String> prefered_activities = new ArrayList<String>();
            for (int i = 0; i < activities.length; i++) {
                boolean preferred = preferences.getBoolean(activities[i], true);
                if (preferred) {
                    prefered_activities.add(activities[i]);
                }
            }
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    amount.clearFocus();
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    String activity = spinner.getSelectedItem().toString();
                    String input = amount.getText().toString();
                    Integer number;
                    if (input.equals("")) {
                        number = 0;
                    } else {
                        number = Integer.parseInt(input);
                    }
                    Integer calories;
                    if (activity.equals("Burn Calories")) {
                        calories = number;
                        output.setText("To burn " + calories.toString() + " Calories,\n you can do the following:");
                        text.setVisibility(View.INVISIBLE);
                    } else {
                        calories = number * 100 / conversions.get(activity);
                        output.setText("You burned " + calories.toString() + " calories!");
                        text.setVisibility(View.INVISIBLE);
                    }
                    List<String> results = new ArrayList<String>();
                    for (int i = 2; i < prefered_activities.size(); i++){
                        String current_activity = prefered_activities.get(i);
                        if (!current_activity.equals(activity)) {
                            Integer conversion = conversions.get(current_activity);
                            Integer current_reps = calories * conversion / 100;
                            if (current_activity.equals(reps[0]) || current_activity.equals(reps[1]) || current_activity.equals(reps[2]) || current_activity.equals(reps[3])) {
                                results.add(current_reps.toString() + " " + current_activity + "!");
                            } else {
                                results.add(current_reps.toString() + " minutes of " + current_activity + "!");
                            }
                        }
                    }
                    ArrayAdapter adapter= new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, results);
                    list_results.setAdapter(adapter);
                    card2.setVisibility(View.VISIBLE);
                    final float scale = getContext().getResources().getDisplayMetrics().density;
                        int pixels = (int) (800 * scale + 0.5f);
                    card2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner_item,  prefered_activities);
            adapter.setDropDownViewResource(R.layout.spinner_layout);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final float scale = getContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (290 * scale + 0.5f);
                    switch (spinner.getItemAtPosition(position).toString()) {
                        case ("Select Activity..."):
                            card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
                            input.setVisibility(View.INVISIBLE);
                            amount.clearComposingText();
                            button.setVisibility(View.INVISIBLE);
                            card2.setVisibility(View.INVISIBLE);
                            card2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
//                            list_results.setVisibility(View.INVISIBLE);
                            break;
                        case ("Burn Calories"):
                            card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
                            input.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            units.setText(R.string.units_3);
                            break;
                        case ("Cycling"):
                        case ("Jogging"):
                        case ("Jumping Jacks"):
                        case ("Leg-Lift"):
                        case ("Plank"):
                        case ("Stair-Climbing"):
                        case ("Walking"):
                        case ("Swimming"):
                            card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
                            input.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            units.setText(R.string.units_2);
                            break;
                        case ("Sit ups"):
                        case ("Push ups"):
                        case ("Pull ups"):
                        case ("Squats"):
                            card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));
                            input.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            units.setText(R.string.units_1);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            return rootView;
        }
    }


}
