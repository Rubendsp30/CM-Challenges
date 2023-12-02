package com.example.challenge3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.scales.DateTime;
import com.google.android.material.slider.RangeSlider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class DataFragment extends Fragment {
    private static final String TOPIC_LED_CONTROL = "/challenge_3/led_control";

    private static final String CHANNEL_ID = "App_3";

    private ReadingsViewModel readingsViewModel;
    private MQTTHelper mqttHelper;

    private ImageButton lightbulbButton;
    private ImageButton humidityButton;
    private ImageButton temperatureButton;
    private TextView humidityValue;
    private TextView temperatureValue;
    private boolean lightState;
    private boolean humidityState;
    private boolean temperatureState;

    private AnyChartView anyChartView;
    private List<DataEntry> humidity_values;
    private List<DataEntry> temperature_values;
    private Line scatterHumidityLine;
    private Line scatterTemperatureLine;

    private LiveData<List<SensorReading>> temperatureLive;
    private LiveData<List<SensorReading>> humidityLive;

    private boolean notificationsEnabled;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    notificationsEnabled = result;
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, container, false);
        this.readingsViewModel = new ViewModelProvider(requireActivity()).get(ReadingsViewModel.class);
        createNotificationsChannel();
        this.humidityLive = readingsViewModel.getHumidityDataFirestore();
        this.temperatureLive = readingsViewModel.getTemperatureDataFirestore();

        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mqttHelper = new MQTTHelper(getContext(), "clientName", TOPIC_LED_CONTROL);
        mqttHelper.connect();

        super.onViewCreated(view, savedInstanceState);

        notificationsEnabled = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API level 33) and above
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationsEnabled = false;
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);

            }
        }


        anyChartView = view.findViewById(R.id.any_chart_view);
        setupChart();

        this.humidityButton = view.findViewById(R.id.humidityButton);
        this.humidityState = true;
        this.temperatureButton = view.findViewById(R.id.temperatureButton);
        this.temperatureState = true;
        this.lightbulbButton = view.findViewById(R.id.lightbulbButton);
        this.lightState = false;

        this.humidityValue = view.findViewById(R.id.humidityValue);
        this.temperatureValue = view.findViewById(R.id.temperatureValue);
        RangeSlider humiditySlider = view.findViewById(R.id.humiditySlider);
        RangeSlider temperatureSlider = view.findViewById(R.id.temperatureSlider);

        List<Float> hum = humiditySlider.getValues();
        this.readingsViewModel.setMinHumidity(Collections.min(hum));
        this.readingsViewModel.setMaxHumidity(Collections.max(hum));

        List<Float> tem = temperatureSlider.getValues();
        this.readingsViewModel.setMinTemperature(Collections.min(tem));
        this.readingsViewModel.setMaxTemperature(Collections.max(tem));


        /* TODO DESCOMENTAR APENAS APÓS DADOS SEREM OBTIDOS DE OUTRA FORMA SENÃO ENTRA EM LOOP INFINITO
        humidityLive.observeForever(sensorReadings -> updateChart());
        temperatureLive.observeForever(sensorReadings -> updateChart());
        */

        // Update Humidity text on change
        humiditySlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();

            humidityValue.setText("Min: "+ String.valueOf(Collections.min(values)) + "%\nMax: "+ String.valueOf(Collections.max(values))+"%");
        });

        //Update the thershold values of humidity
        humiditySlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                // Responds to when slider's touch event is being stopped
                List<Float> values = slider.getValues();
                readingsViewModel.setMaxHumidity(Collections.max(values));
                readingsViewModel.setMinHumidity(Collections.min(values));
            }
        });

        temperatureSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();

            temperatureValue.setText("Min: "+ String.valueOf(Collections.min(values)) + "ºC\nMax: "+ String.valueOf(Collections.max(values))+"ºC");
        });

        temperatureSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                // Responds to when slider's touch event is being stopped
                List<Float> values = slider.getValues();
                readingsViewModel.setMaxTemperature(Collections.max(values));
                readingsViewModel.setMinTemperature(Collections.min(values));
            }
        });


        this.humidityButton.setOnClickListener(v -> updateHumidityState());
        this.temperatureButton.setOnClickListener(v -> updateTemperatureState());
        this.lightbulbButton.setOnClickListener(v -> {
            MQTTHelper mqttHelper = new MQTTHelper(getContext(), "clientName", TOPIC_LED_CONTROL);
            mqttHelper.connect();
            mqttHelper.publishToTopic(TOPIC_LED_CONTROL, lightState ? "OFF" : "ON");
            updateLight();
            updateChart();
        });
        updateLight();
        // TODO *********************************************Code only used for testing******************************************
        updateChart();
        // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    };



    private void updateHumidityState() {
        if (humidityState) {
            this.humidityButton.setImageResource(R.drawable.water_drop_off);
        } else {
            this.humidityButton.setImageResource(R.drawable.water_drop_on);
        }
        this.humidityState = !this.humidityState;
    }

    private void updateTemperatureState() {
        if (temperatureState) {
            this.temperatureButton.setImageResource(R.drawable.thermostat_off);
        } else {
            this.temperatureButton.setImageResource(R.drawable.thermostat_on);
        }
        this.temperatureState = !this.temperatureState;
    }

    private void updateLight() {
        lightState = !lightState;
        if (lightState) {
            this.lightbulbButton.setImageResource(R.drawable.lightbulb_on);
            mqttHelper.publishToTopic(TOPIC_LED_CONTROL, "ON");
        } else {
            this.lightbulbButton.setImageResource(R.drawable.lightbulb_off);
            mqttHelper.publishToTopic(TOPIC_LED_CONTROL, "OFF");
        }
    }




    private void setupChart() {

        Scatter scatter = AnyChart.scatter();
        scatter.noData().label().enabled(true).text("No data. Turn Arduino on");
        scatter.animation(true);
        scatter.tooltip(true);

        scatter.legend().enabled(true);
        scatter.legend().fontSize(13d);
        scatter.legend().padding(0d, 0d, 10d, 0d);

        scatter.xScale(DateTime.instantiate());
        scatter.xAxis(0).labels().format("{%value}{dateTimeFormat:MMM d HH:mm:ss}");

        scatterHumidityLine = scatter.line(humidity_values);
        scatterTemperatureLine = scatter.line(temperature_values);

        scatterHumidityLine.stroke("Aqua", 3d, null, (String) null, (String) null);
        scatterTemperatureLine.stroke("DarkOrange", 3d, null, (String) null, (String) null);

        scatterHumidityLine.name("Humidity");
        scatterHumidityLine.markers().enabled(true).type(MarkerType.CIRCLE).size(5d);
        scatterHumidityLine.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        scatterHumidityLine.tooltip().titleFormat("{%x}{dateTimeFormat:MMM d HH:mm:ss}");
        scatterHumidityLine.tooltip().format("{%value}%");

        scatterTemperatureLine.name("Temperature");
        scatterTemperatureLine.markers().enabled(true).type(MarkerType.CIRCLE).size(5d);
        scatterTemperatureLine.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        scatterTemperatureLine.tooltip().titleFormat("{%x}{dateTimeFormat:MMM d HH:mm:ss}");
        scatterTemperatureLine.tooltip().format("{%value}ºC");


        anyChartView.setChart(scatter);

    }

    private void updateChart() {
        humidity_values = new ArrayList<>();
        temperature_values = new ArrayList<>();

        // TODO *********************************************Code only used for testing******************************************
        addData();
        // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        for (SensorReading reading : readingsViewModel.getHumidityData()) {
            humidity_values.add(new ValueDataEntry(String.valueOf(reading.getTimestamp().getTime()), reading.getSensorReading().floatValue()));
        }
        for (SensorReading reading : readingsViewModel.getTemperatureData()) {
            temperature_values.add(new ValueDataEntry(String.valueOf(reading.getTimestamp().getTime()), reading.getSensorReading().floatValue()));
        }

        scatterHumidityLine.data(humidity_values);
        scatterTemperatureLine.data(temperature_values);

    }

    // TODO *********************************************Code only used for testing******************************************
    private void addData() {
        Random random = new Random();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
        //Humidity
        double yValueH = random.nextDouble() * 100; // Generate a random y value between -20 and 80
        double formattedYValueH = Double.parseDouble(decimalFormat.format(yValueH));
        SensorReading sensorReadingH = new SensorReading(formattedYValueH);
        readingsViewModel.addHumidityLiveData(sensorReadingH);

        //Todo move this if condition to when receive the value from mqtt
        if ((yValueH > readingsViewModel.getMaxHumidity() || yValueH < readingsViewModel.getMinHumidity())&& notificationsEnabled) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.water_drop_on)
                    .setContentTitle("Humidity Warning!")
                    .setContentText("Humidity out of allowed range.")
                    .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            }


        }

        //temperature
        double yValueT = random.nextDouble() * (80 + 20) - 20; // Generate a random y value between -20 and 80
        double formattedYValueT = Double.parseDouble(decimalFormat.format(yValueT));
        SensorReading sensorReadingT = new SensorReading(formattedYValueT);
        readingsViewModel.addTemperatureLiveData(sensorReadingT);

        //Todo move this if condition to when receive the value from mqtt
        if ((yValueT > readingsViewModel.getMaxTemperature() || yValueT < readingsViewModel.getMinTemperature()) && notificationsEnabled) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.thermostat_on)
                    .setContentTitle("Temperature Warning!")
                    .setContentText("Temperature out of allowed range.")
                    .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(2, builder.build());
            }

        }
    }
    // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Challenge3", importance);
        channel.setDescription("Challenge Notifications");
        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
