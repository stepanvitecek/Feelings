package eu.vstepik.feelings.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.vstepik.feelings.R;
import eu.vstepik.feelings.data.Feeling;
import eu.vstepik.feelings.data.Feeling.Feelings;
import eu.vstepik.feelings.notify.AlarmSetter;
import eu.vstepik.feelings.ui.widgets.FeelingsChart;

/**
 *
 * Graph of recent feelings
 *
 * @author vstepik, destil
 */

public class MainActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = "FeelingsActivity";
    private MainActivity c = this;
    private List<Feeling> feelings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();
        updateOnDataChanges();
        setPeriodicNotifications();
    }

    /**
     * Set up tabs
     */
    private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Graph tab
        Tab tab1 = actionBar.newTab();
        tab1.setText(R.string.graph);
        tab1.setTabListener(new TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                setContentView(R.layout.tab_graph);
                new GraphTask().execute();
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        });
        actionBar.addTab(tab1);
        // Stats tab
        Tab tab2 = actionBar.newTab();
        tab2.setText(R.string.stats);
        tab2.setTabListener(new TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                setContentView(R.layout.tab_stats);
                new StatsTask().execute();
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        });
        actionBar.addTab(tab2);
    }

    /**
     * Defining menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * When menu clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.authors:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.new_feeling:
                startActivity(new Intent(this, NewFeelingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listen for data changes
     */
    private void updateOnDataChanges() {
        getContentResolver().registerContentObserver(Feelings.CONTENT_URI, true, new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                new GraphTask().execute();
            }
        });
    }

    /**
     * Sets up notifications for every day.
     */
    private void setPeriodicNotifications() {
        sendBroadcast(new Intent(this, AlarmSetter.class));
    }

    /**
     * Constructs graph.
     *
     * @author vstepik, destil
     */
    class GraphTask extends AsyncTask<Void, Void, XYMultipleSeriesDataset> {

        @Override
        protected XYMultipleSeriesDataset doInBackground(Void... params) {
            feelings = Feelings.getAll(c);
            TimeSeries series = new TimeSeries("Feeling");
            for (Feeling feeling : feelings) {
                series.add(new Date(feeling.created), feeling.value);
            }
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
            dataset.addSeries(series);
            return dataset;
        }

        @Override
        protected void onPostExecute(XYMultipleSeriesDataset dataset) {
            FeelingsChart feelingsChart = new FeelingsChart(dataset);
            GraphicalView chartView = new GraphicalView(c, feelingsChart);
            LinearLayout chart = (LinearLayout) findViewById(R.id.chart);
            if (chart.getChildCount() > 0) {
                chart.removeAllViews();
            }
            chart.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    /**
     * Tasks which calculate stats.
     *
     * @author vstepik, destil
     */
    class StatsTask extends AsyncTask<Void, Void, Void> {

        List<DayOfWeekStat> dayOfWeekStats;

        @Override
        protected Void doInBackground(Void... params) {
            // average for each day of week
            Calendar calendar = new GregorianCalendar();
            dayOfWeekStats = new ArrayList<DayOfWeekStat>();
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.MONDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.TUESDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.WEDNESDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.THURSDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.FRIDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.SATURDAY));
            dayOfWeekStats.add(new DayOfWeekStat(Calendar.SUNDAY));
            for (Feeling feeling : feelings) {
                calendar.setTimeInMillis(feeling.created);
                for (DayOfWeekStat dayOfWeekStat : dayOfWeekStats) {
                    if (dayOfWeekStat.day == calendar.get(Calendar.DAY_OF_WEEK)) {
                        dayOfWeekStat.sum += feeling.value;
                        dayOfWeekStat.count += 1;
                        break;
                    }
                }
            }
            Collections.sort(dayOfWeekStats);
            return null;
        }

        @Override
        protected void onPostExecute(Void aaa) {
            LinearLayout bestDays = (LinearLayout) findViewById(R.id.best_days);
            bestDays.removeAllViews();
            int i=1;
            for (DayOfWeekStat dayOfWeekStat : dayOfWeekStats) {
                TextView textView = new TextView(c);
                textView.setText(i+++". "+dayOfWeekStat.getDayName());
                bestDays.addView(textView);
            }
        }
    }

    class DayOfWeekStat implements Comparable<DayOfWeekStat> {
        public int day;
        public int sum;
        public int count;

        public DayOfWeekStat(int day) {
            this.day = day;
        }

        public double getAverage() {
            return (double) sum / count;
        }

        public String getDayName() {
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.DAY_OF_WEEK, this.day);
            return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        }

        @Override
        public int compareTo(DayOfWeekStat another) {
            return (int) ((another.getAverage() - this.getAverage()) * 1000);
        }
    }
}