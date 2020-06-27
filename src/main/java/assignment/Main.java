package assignment;

import java.io.*;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;

public class Main {
    public static void main( String[] args ) {
        URL url;
        try {
            if(args.length == 2) {
                url = new URL(
                        "https://api.covid19api.com/country/canada?from=" + args[0] +
                                "&to=" + args[1]);
            } else {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime then = now.minusDays(7);
                url = new URL(
                        "https://api.covid19api.com/country/canada?from=" + then.format(format) +
                        "&to=" + now.format(format));
            }
            System.out.println("URL: " + url);

            String res = IOUtils.toString(url, "UTF-8");

            JSONArray array = new JSONArray(res);
            System.out.println("Number of records: " + array.length());

            // construct list of CSV entry objects
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                String date = (String) jsonObject.get("Date");
                String province = (String) jsonObject.get("Province");
                int confirmed = (int) jsonObject.get("Confirmed");
                int deaths = (int) jsonObject.get("Deaths");;
                int recovered = (int) jsonObject.get("Recovered");;
                int active = (int) jsonObject.get("Active");;
                entries.add(new Entry(date, province, confirmed, deaths, recovered, active));
            }

            // calculate new cases
            Map<String, List<Entry>> entriesPerProvince = entries.stream()
                    .collect(groupingBy(Entry::getProvince));
            entriesPerProvince.forEach((k, v)-> {
                // skip the first date
                if(v.size() < 2) return;

                Collections.sort(v, Comparator.comparing(Entry::getDate));
                for(int i = 1; i < v.size(); i++) {
                    v.get(i).setNewCases(v.get(i).getConfirmed() - v.get(i-1).getConfirmed());
                }
            });

            // sort by province and date
            Collections.sort(entries, Comparator.comparing(Entry::getProvince).thenComparing(Entry::getDate));

            List<String> entriesAsString = entries.stream()
                    .map(Entry::toString).collect(Collectors.toList());

            // write to CSV file
            String fileName = "COVID19_data.csv";
            File file = new File(fileName);
            String CSVString;
            if (file.createNewFile()) {
                System.out.println("File created: " + fileName);

                CSVString = entriesAsString.stream().collect(Collectors.joining(System.getProperty("line.separator")));
                //System.out.println(CSVString);

                FileWriter writer = new FileWriter(file.getName());
                writer.write("Date,Province,Confirmed,Deaths,Recovered,Active,New Cases" +
                                System.getProperty("line.separator"));
                writer.write(CSVString + System.getProperty("line.separator"));
                writer.close();
            } else {
                // read in existing entries
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line = reader.readLine();
                while (line != null) {
                    for (int i = 0; i < entriesAsString.size(); i++) {
                        // exclude new cases in equality check and remove existing entry
                        String[] entrySplit = entriesAsString.get(i).split(",");
                        String[] lineSplit = line.split(",");
                        if(Arrays.equals(Arrays.copyOf(entrySplit, entrySplit.length - 1),
                                         Arrays.copyOf(lineSplit, lineSplit.length - 1))) {
                            entriesAsString.remove(i);
                        }
                    }
                    line = reader.readLine();
                }
                reader.close();

                CSVString = entriesAsString.stream().collect(Collectors.joining(System.getProperty("line.separator")));
                System.out.println("Number of new records: " + entriesAsString.size());
                //System.out.println(CSVString);

                FileWriter fileWriter = new FileWriter(fileName, true);
                System.out.println("File already exists. Appending to file...");
                PrintWriter printWriter = new PrintWriter(fileWriter);
                if(!CSVString.isEmpty())
                    printWriter.println(CSVString);
                printWriter.close();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

