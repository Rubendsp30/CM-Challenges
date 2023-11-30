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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class DataFragment extends Fragment {

    private static final String CHANNEL_ID = "App_3";

    private ReadingsViewModel readingsViewModel;
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

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        // PERMISSION GRANTED
                        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                        notificationsEnabled = true;
                    } else {
                        // PERMISSION NOT GRANTED
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        notificationsEnabled = false;
                    }
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
        SeekBar humiditySlider = view.findViewById(R.id.humiditySlider);
        SeekBar temperatureSlider = view.findViewById(R.id.temperatureSlider);

        this.readingsViewModel.setMaxHumidity(70.0);
        this.readingsViewModel.setMaxTemperature(40.0);

        /* TODO DESCOMENTAR APENAS APÓS DADOS SEREM OBTIDOS DE OUTRA FORMA SENÃO ENTRA EM LOOP INFINITO
        humidityLive.observeForever(sensorReadings -> updateChart());
        temperatureLive.observeForever(sensorReadings -> updateChart());
        */

        humiditySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress changes here with the step size applied
                double value = progress * 0.5;
                if (value == (int) value) {
                    humidityValue.setText(String.valueOf((int) value) + "%");
                } else {
                    humidityValue.setText(String.valueOf(value) + "%");
                }
                // Update a TextView or perform any other actions with the value
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle touch start
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                readingsViewModel.setMaxHumidity(seekBar.getProgress() * 0.5);
                // Handle touch end
            }
        });

        temperatureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress changes here with the step size applied
                double value = progress * 0.5;
                if (value == (int) value) {
                    temperatureValue.setText(String.valueOf((int) value) + "ºC");
                } else {
                    temperatureValue.setText(String.valueOf(value) + "ºC");
                }
                // Update a TextView or perform any other actions with the value
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle touch start
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                readingsViewModel.setMaxTemperature(seekBar.getProgress() * 0.5);
                // Handle touch end
            }
        });


        this.humidityButton.setOnClickListener(v -> updateHumidityState());
        this.temperatureButton.setOnClickListener(v -> updateTemperatureState());
        this.lightbulbButton.setOnClickListener(v -> {
            updateLight();
            // TODO *********************************************Code only used for testing******************************************
            updateChart();
            // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        });
    }


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
        if (lightState) {
            this.lightbulbButton.setImageResource(R.drawable.lightbulb_on);
        } else {
            this.lightbulbButton.setImageResource(R.drawable.lightbulb_off);
        }
        this.lightState = !this.lightState;
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
        if (yValueH > readingsViewModel.getMaxHumidity() && notificationsEnabled) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.water_drop_on)
                    .setContentTitle("Humidity Warning!")
                    .setContentText("Humidity too high!")
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
        if (yValueT > readingsViewModel.getMaxTemperature() && notificationsEnabled) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.thermostat_on)
                    .setContentTitle("Temperature Warning!")
                    .setContentText("Temperature too high!")
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
