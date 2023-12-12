package com.example.challenge3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DataFragment extends Fragment {

    private static final String CHANNEL_ID = "App_3";
    private static final String TOPIC_LED_CONTROL = "/challenge_3/light";
    public static final String HUMIDITY_TOPIC = "/challenge_3/humidity";
    public static final String TEMPERATURE_TOPIC = "/challenge_3/temperature";
    private MQTTHelper mqttHelper;

    private ReadingsViewModel readingsViewModel;
    private ImageButton humidityButton;
    private ImageButton temperatureButton;
    private TextView humidityValue;
    private TextView temperatureValue;
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
        this.mqttHelper = setupMqtt();
        this.readingsViewModel = new ViewModelProvider(requireActivity()).get(ReadingsViewModel.class);
        createNotificationsChannel();
        this.humidityLive = readingsViewModel.getHumidityDataFirestore();
        this.temperatureLive = readingsViewModel.getTemperatureDataFirestore();

        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        ImageButton lightbulbOnButton = view.findViewById(R.id.lightbulbOnButton);
        ImageButton lightbulbOffButton = view.findViewById(R.id.lightbulbOffButton);

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

            humidityValue.setText("Min: " + String.valueOf(Collections.min(values)) + "%\nMax: " + String.valueOf(Collections.max(values)) + "%");
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

            temperatureValue.setText("Min: " + String.valueOf(Collections.min(values)) + "ºC\nMax: " + String.valueOf(Collections.max(values)) + "ºC");
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
        lightbulbOnButton.setOnClickListener(v -> {
            mqttHelper.publishToTopic(TOPIC_LED_CONTROL,"on",2);
        });
        lightbulbOffButton.setOnClickListener(v -> {
            mqttHelper.publishToTopic(TOPIC_LED_CONTROL,"off",2);
        });
    }


    private void updateHumidityState() {
        if (humidityState) {
            this.humidityButton.setImageResource(R.drawable.water_drop_off);
            mqttHelper.unsubscribeToTopic(HUMIDITY_TOPIC);
        } else {
            this.humidityButton.setImageResource(R.drawable.water_drop_on);
            mqttHelper.subscribeToTopic(HUMIDITY_TOPIC);
        }
        this.humidityState = !this.humidityState;
    }

    private void updateTemperatureState() {
        if (temperatureState) {
            this.temperatureButton.setImageResource(R.drawable.thermostat_off);
            mqttHelper.unsubscribeToTopic(TEMPERATURE_TOPIC);
        } else {
            this.temperatureButton.setImageResource(R.drawable.thermostat_on);
            mqttHelper.subscribeToTopic(TEMPERATURE_TOPIC);
        }
        this.temperatureState = !this.temperatureState;
    }

    private MQTTHelper setupMqtt() {
        mqttHelper = new MQTTHelper(requireContext(), "ClientName", "challenge_3");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("MQTT", "CONNECTED: "+serverURI);
                mqttHelper.subscribeToTopic("/challenge_3/temperature");
                mqttHelper.subscribeToTopic("/challenge_3/humidity");
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTT", "CONNECTION LOST");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                SensorReading sensorReading = new SensorReading(Double.parseDouble(message.toString()));
                Log.e("Msg", sensorReading.toString());
                if (topic.equals(HUMIDITY_TOPIC)) {
                    readingsViewModel.addHumidityLiveData(sensorReading);
                    if ((sensorReading.getSensorReading() < readingsViewModel.getMinHumidity() || sensorReading.getSensorReading() > readingsViewModel.getMaxHumidity())&& notificationsEnabled) {
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
                    updateChart();
                }
                if (topic.equals(TEMPERATURE_TOPIC)) {
                    readingsViewModel.addTemperatureLiveData(sensorReading);
                    if ((sensorReading.getSensorReading() < readingsViewModel.getMinTemperature() || sensorReading.getSensorReading() > readingsViewModel.getMaxTemperature()) && notificationsEnabled) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.thermostat_on)
                                .setContentTitle("Temperature Warning!")
                                .setContentText("Temperature out of allowed range.")
                                .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            notificationManager.notify(1, builder.build());
                        }
                    }
                    updateChart();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT", "DELIVERY COMPLETED");
            }
        });

        mqttHelper.connect();
        return mqttHelper;
    }



    private void setupChart() {

        Scatter scatter = AnyChart.scatter();
        scatter.noData().label().enabled(true).text("No data. Turn Arduino on");
        scatter.animation(true);
        scatter.tooltip(true);

        scatter.legend().enabled(true);
        scatter.legend().fontSize(13d);
        scatter.legend().padding(0d, 10d, 0d, 0d);
        scatter.background().fill("#F8FCFC");

        scatter.xScale(DateTime.instantiate());
        //scatter.xScale().alignMaximum(true);
        //scatter.xScale().alignMinimum(true);
        scatter.xAxis(0).labels().format("{%tickValue}{dateTimeFormat:MMM d HH:mm:ss}");

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

        for (SensorReading reading : readingsViewModel.getHumidityData()) {
            humidity_values.add(new ValueDataEntry(String.valueOf(reading.getTimestamp().getTime()), reading.getSensorReading().floatValue()));
        }
        for (SensorReading reading : readingsViewModel.getTemperatureData()) {
            temperature_values.add(new ValueDataEntry(String.valueOf(reading.getTimestamp().getTime()), reading.getSensorReading().floatValue()));
        }

        scatterHumidityLine.data(humidity_values);
        scatterTemperatureLine.data(temperature_values);

    }

    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Challenge3", importance);
        channel.setDescription("Challenge Notifications");
        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
