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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataFragment extends Fragment {
    private AnyChartView anyChartView;
    private ImageButton lightbulbButton;
    private ImageButton humidityButton;
    private ImageButton temperatureButton;
    private boolean lightState;
    private boolean humidityState;
    private boolean temperatureState;

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

        // TODO *********************************************Code only used for testing******************************************
        List<DataEntry> humidity_values = new ArrayList<>();
        List<DataEntry> temperature_values = new ArrayList<>();

        // TODO *Create Data*
        Random random = new Random();
        Instant baseTimestamp = Instant.parse("2023-01-01T00:00:00Z");
        //scatter.xScale(DateTime.instantiate());
        //scatter.xAxis(0).labels().format("{%value}{dateTimeFormat:MMM d HH:mm:ss}");

        for (int i = 0; i < 10; i++) {
            //Instant timestamp = baseTimestamp.plusSeconds(i * 600); // 600 seconds = 10 minutes
            Instant timestamp = baseTimestamp.plusSeconds(i * 36000);


            double yValue = random.nextDouble() * (80+20) -20; // Generate a random y value between -20 and 80
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double formattedYValue = Double.parseDouble(decimalFormat.format(yValue));

            humidity_values.add(new ValueDataEntry(String.valueOf(Timestamp.from(timestamp)), formattedYValue));
        }

        baseTimestamp = Instant.parse("2023-01-01T00:00:00Z");
        for (int i = 0; i < 10; i++) {
            //Instant timestamp = baseTimestamp.plusSeconds(i * 600); // 600 seconds = 10 minutes
            Instant timestamp = baseTimestamp.plusSeconds(i * 36060);


            double yValue = random.nextDouble() * 100; // Generate a random y value between 0 and 10
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double formattedYValue = Double.parseDouble(decimalFormat.format(yValue));

            temperature_values.add(new ValueDataEntry(String.valueOf(Timestamp.from(timestamp)), formattedYValue));
        }
        // TODO ^Create Data^

        Line scatterHumidityLine = scatter.line(temperature_values);
        Line scatterTemperatureLine = scatter.line(humidity_values);

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

        // TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Code only used for testing^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        anyChartView.setChart(scatter);

    }


}
