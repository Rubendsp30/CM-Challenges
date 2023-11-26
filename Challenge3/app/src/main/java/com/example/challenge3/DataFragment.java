package com.example.challenge3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.scales.DateTime;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class DataFragment extends Fragment {
    private AnyChartView anyChartView;
    private ImageButton lightbulbButton;
    private ImageButton humidityButton;
    private ImageButton temperatureButton;
    private boolean lightState;
    private boolean humidityState;
    private boolean temperatureState;
    private List<DataEntry> humidity_values;
    private List<DataEntry> temperature_values;
    private Line scatterHumidityLine;
    private Line scatterTemperatureLine;
    // TODO *********************************************Code only used for testing******************************************
    private List<DataEntry> humidity_values_full = new ArrayList<>();
    private List<DataEntry> temperature_values_full= new ArrayList<>();
    // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, container, false);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anyChartView = view.findViewById(R.id.any_chart_view);
        setupChart();

        this.humidityButton = view.findViewById(R.id.humidityButton);
        this.humidityState = true;
        this.temperatureButton = view.findViewById(R.id.temperatureButton);
        this.temperatureState = true;
        this.lightbulbButton = view.findViewById(R.id.lightbulbButton);
        this.lightState = false;


        this.humidityButton.setOnClickListener(v -> {
            updateHumidityState();
        });
        this.temperatureButton.setOnClickListener(v -> {
            updateTemperatureState();
        });
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
        scatterHumidityLine.markers().enabled(true).type(MarkerType.CIRCLE).size(3.5d);
        scatterHumidityLine.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        scatterHumidityLine.tooltip().titleFormat("{%x}{dateTimeFormat:MMM d HH:mm:ss}");
        scatterHumidityLine.tooltip().format("{%value}%");

        scatterTemperatureLine.name("Temperature");
        scatterTemperatureLine.markers().enabled(true).type(MarkerType.CIRCLE).size(3.5d);
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
        for (DataEntry entry : humidity_values_full) {
            humidity_values.add(entry);
        }

        for (DataEntry entry : temperature_values_full) {
            temperature_values.add(entry);
        }
        // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
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
        humidity_values_full.add(new ValueDataEntry(String.valueOf(Timestamp.from(Instant.now())), formattedYValueH));
        //temperature
        double yValueT = random.nextDouble() * (80 + 20) - 20; // Generate a random y value between -20 and 80
        double formattedYValueT = Double.parseDouble(decimalFormat.format(yValueT));
        temperature_values_full.add(new ValueDataEntry(String.valueOf(Timestamp.from(Instant.now())), formattedYValueT));
    }
    // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

}
