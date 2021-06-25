package sk.fri.uniza;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sk.fri.uniza.api.WeatherStationService;
import sk.fri.uniza.model.WeatherData;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class IotNode {

    private final Retrofit retrofit;
    private final WeatherStationService weatherStationService;

    public IotNode() {

        retrofit = new Retrofit.Builder()
                // Url adresa kde je umietnená WeatherStation služba
                .baseUrl("http://localhost:9000/")
                // Na konvertovanie JSON objektu na java POJO použijeme
                // Jackson knižnicu
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        // Vytvorenie inštancie komunikačného rozhrania
        weatherStationService = retrofit.create(WeatherStationService.class);

    }

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    public double getAverageTemperature(String station, String from, String to)
    {
        double sum;
        Call<List<WeatherData>> historyFields =
                getWeatherStationService().getHistoryWeatherFields("station_1","23/06/2021 15:00","24/06/2021 15:00",List.of("airTemperature"));

        try {
            Response<List<WeatherData>> response = historyFields.execute();

            if (response.isSuccessful()) { // Dotaz na server bol neúspešný
                //Získanie údajov vo forme Zoznam lokacií
                List<WeatherData> body = response.body();

                sum = body.stream().mapToDouble(WeatherData::getAirTemperature).sum();
                sum = sum/body.size();
                System.out.println(body);
                return sum;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

}
