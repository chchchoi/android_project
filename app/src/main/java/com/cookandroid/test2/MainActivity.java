package com.cookandroid.test2;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button fetchButton;
    private ListView scheduleListView;
    //private ArrayAdapter<String> adapter;
    private ScheduleAdapter adapter;
    //private ArrayList<String> scheduleList;
    private ArrayList<ScheduleItem> scheduleList;
    private Button showWebViewButton, btnPlayer, btnWeather;
    private WebView webView;

    CardView[] cardViews = new CardView[5];
    TextView[] textStadium = new TextView[5];
    TextView[] textTemp = new TextView[5];
    ImageView[] imgViews = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchButton = findViewById(R.id.button_fetch_schedule);
        scheduleListView = findViewById(R.id.listview_schedule);
        showWebViewButton = findViewById(R.id.button_show_webview);
        webView = findViewById(R.id.webview);
        btnPlayer = findViewById(R.id.btnPlayer);
        btnWeather = findViewById(R.id.btnweather);
        textStadium[0] =findViewById(R.id.textStadium1);
        textStadium[1] =findViewById(R.id.textStadium2);
        textStadium[2] =findViewById(R.id.textStadium3);
        textStadium[3] =findViewById(R.id.textStadium4);
        textStadium[4] =findViewById(R.id.textStadium5);
        textTemp[0] = findViewById(R.id.textTemp1);
        textTemp[1] = findViewById(R.id.textTemp2);
        textTemp[2] = findViewById(R.id.textTemp3);
        textTemp[3] = findViewById(R.id.textTemp4);
        textTemp[4] = findViewById(R.id.textTemp5);
        imgViews[0] = findViewById(R.id.weatherimg1);
        imgViews[1] = findViewById(R.id.weatherimg2);
        imgViews[2] = findViewById(R.id.weatherimg3);
        imgViews[3] = findViewById(R.id.weatherimg4);
        imgViews[4] = findViewById(R.id.weatherimg5);
        cardViews[0] = findViewById(R.id.card1);
        cardViews[1] = findViewById(R.id.card2);
        cardViews[2] = findViewById(R.id.card3);
        cardViews[3] = findViewById(R.id.card4);
        cardViews[4] = findViewById(R.id.card5);


        scheduleList = new ArrayList<>();
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
        adapter = new ScheduleAdapter(this, scheduleList);
        scheduleListView.setAdapter(adapter);
        showWebViewButton.setBackgroundColor(Color.GRAY);
        fetchButton.setOnClickListener(v -> {
            scheduleListView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            for(int i=0;i<5;i++){
                cardViews[i].setVisibility(View.GONE);
            }
            FetchScheduleTask task = new FetchScheduleTask();
            task.execute();
            showWebViewButton.setBackgroundColor(Color.GRAY);
            fetchButton.setBackgroundColor(Color.rgb(226, 185, 199));
        });
        showWebViewButton.setOnClickListener(v -> {
            scheduleListView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            for(int i=0;i<5;i++){
                cardViews[i].setVisibility(View.GONE);
            }
            webView.loadUrl("https://m.sports.naver.com/kbaseball/record/index.nhn?category=kbo&tab=team");
            showWebViewButton.setBackgroundColor(Color.rgb(226, 185, 199));
            fetchButton.setBackgroundColor(Color.GRAY);
        });
        btnPlayer.setOnClickListener(v ->{
            scheduleListView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            for(int i=0;i<5;i++){
                cardViews[i].setVisibility(View.GONE);
            }
            webView.loadUrl("https://m.sports.naver.com/kbaseball/record/index.nhn?category=kbo&tab=player");
        });
        btnWeather.setOnClickListener(v -> {
            scheduleListView.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            for(int i=0;i<5;i++){
                cardViews[i].setVisibility(View.VISIBLE);
                FetchScheduleTask2 task2 = new FetchScheduleTask2();
                task2.execute();
            }


        });
        FetchScheduleTask task = new FetchScheduleTask();
        task.execute();
    }

    private class FetchScheduleTask extends AsyncTask<Void, Void, Void> {

        private String response;


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줌
        String month2 = String.format("%02d", month);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //int hour = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        String day = String.format("%02d", dayOfMonth);

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.koreabaseball.com/ws/Main.asmx/GetKboGameList");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setDoOutput(true);

                String data = "leId=1&srId=0&srId=1&srId=3&srId=4&srId=5&srId=7&srId=9&date="+year+month2+day;
                byte[] postDataBytes = data.getBytes("UTF-8");
                conn.getOutputStream().write(postDataBytes);


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                this.response = response.toString();

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray games = jsonResponse.getJSONArray("game");

                    scheduleList.clear();
                    for (int i = 0; i < games.length(); i++) {
                        JSONObject game = games.getJSONObject(i);
                        String awayTeam = game.getString("AWAY_NM");
                        String homeTeam = game.getString("HOME_NM");
                        String awayScore = game.getString("T_SCORE_CN");
                        String homeScore = game.getString("B_SCORE_CN");
                        //String schedule = "Game " + (i + 1) + ": Away Team - " + awayTeam + ", Home Team - " + homeTeam;
                        int awayImageResource = getTeamImageResource(awayTeam);
                        int homeImageResource = getTeamImageResource(homeTeam);

                        ScheduleItem item = new ScheduleItem(awayTeam, homeTeam, awayImageResource, homeImageResource, awayScore, homeScore);
                        scheduleList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        private int getTeamImageResource(String teamName) {
            switch (teamName) {
                case "키움":
                    return R.drawable.wo;
                case "LG":
                    return R.drawable.lg;
                case "한화":
                    return R.drawable.hh;
                case "KIA":
                    return R.drawable.ht;
                case "KT":
                    return R.drawable.kt;
                case "롯데":
                    return R.drawable.lt;
                case "NC":
                    return R.drawable.nc;
                case "두산":
                    return R.drawable.ob;
                case "SSG":
                    return R.drawable.sk;
                case "삼성":
                    return R.drawable.ss;
                default:
                    return R.drawable.kbo;
            }
        }

    }

    //////
    private class FetchScheduleTask2 extends AsyncTask<Void, Void, Void> {

        private String response;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줌
        String month2 = String.format("%02d", month);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //int hour = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        String day = String.format("%02d", dayOfMonth);

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.koreabaseball.com/ws/Schedule.asmx/GetTodayGames");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setDoOutput(true);

                String data = "gameDate="+year+month2+day+"&leID=1&srId=0&srId=1&srId=2&srId=3&srId=4&srId=5&srId=6&srId=7&srId=8&srId=9&headerCk=1";
                byte[] postDataBytes = data.getBytes("UTF-8");
                conn.getOutputStream().write(postDataBytes);


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                this.response = response.toString();

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray gameLists = jsonResponse.getJSONArray("gameList");

                    for (int i = 0; i < gameLists.length(); i++) {
                        JSONObject gameList = gameLists.getJSONObject(i);
                        String stadiumFullName = gameList.getString("stadiumFullName");
                        String temp = gameList.getString("temp");
                        String dust = gameList.getString("dust");
                        String iconName = gameList.getString("iconName");
                        int weatherImageResource = getweatherImageResource(iconName);

                        textStadium[i].setText(stadiumFullName);
                        textTemp[i].setText(temp + "°C\t 미세먼지 " + dust);
                        imgViews[i].setImageResource(weatherImageResource);
                        cardViews[i].setVisibility(View.VISIBLE);

                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        private int getweatherImageResource(String iconname) {
            switch (iconname) {
                case "맑음":
                    return R.drawable.sun;
                case "구름조금":
                    return R.drawable.cloud;
                case "구름많음":
                    return R.drawable.cloudy;
                case "흐림":
                    return R.drawable.wind;
                case "흐리고 비":
                    return R.drawable.rainy;

                default:
                    return R.drawable.sun;
            }
        }

    }
    //////
    public class ScheduleAdapter extends ArrayAdapter<ScheduleItem> {

        public ScheduleAdapter(Context context, ArrayList<ScheduleItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScheduleItem item = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_item, parent, false);
            }

            ImageView awayImageView = convertView.findViewById(R.id.awayImageView);
            ImageView homeImageView = convertView.findViewById(R.id.homeImageView);
            TextView textView = convertView.findViewById(R.id.textView);

            awayImageView.setImageResource(item.getAwayImageResource());
            homeImageView.setImageResource(item.getHomeImageResource());

            textView.setText(item.getAwayScore() + " vs " + item.getHomeScore());

            return convertView;
        }
    }
    public static class ScheduleItem {
        private String awayTeam;
        private String homeTeam;
        private int awayImageResource;
        private int homeImageResource;
        private String awayScore;
        private String homeScore;

        ScheduleItem(String awayTeam, String homeTeam, int awayImageResource, int homeImageResource, String awayScore, String homeScore) {
            this.awayTeam = awayTeam;
            this.homeTeam = homeTeam;
            this.awayImageResource = awayImageResource;
            this.homeImageResource = homeImageResource;
            this.awayScore = awayScore;
            this.homeScore = homeScore;
        }

        String getAwayScore() {
            return awayScore;
        }

        String getAwayTeam(){return awayTeam;}
        String getHomeTeam() { return homeTeam; }
        String getHomeScore() {
            return homeScore;
        }

        int getAwayImageResource() {
            return awayImageResource;
        }

        int getHomeImageResource() {
            return homeImageResource;
        }
    }

}