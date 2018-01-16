import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WeatherReport {

	public static final String [] regions = {"UK","England","Wales","Scotland"};
	public static final String [] params = { "Tmax", "Tmin", "Tmean", "Sunshine", "Rainfall" };
	public static final String base_url = "https://www.metoffice.gov.uk/pub/data/weather/uk/climate/datasets/";
	public static final String directory_name = "C:\\KisanHubAssignment\\src\\";
	public static final String date = "/date/";
	private static FileWriter file_writer;
	private static String line = null;
	private static final String NOT_AVAILABLE = "N/A";
	private static String[] keys;


	/*
	 * This function makes the new Directory with the Region name and store all the download text files there. 
	 */
	public static void Download_Weather_Report_Files(){

		for(int i = 0 ; i < regions.length ; i++){

			for(int j = 0 ; j < params.length ; j++){

				String url = base_url + params[j] + date + regions[i] + ".txt";

				File dir = new File(directory_name+regions[i]);
				dir.mkdir();

				String path_to_store = directory_name + regions[i] + "\\" + regions[i]+"_"+params[j]+".txt";
//				System.out.println(url);

				try {
					download(url,path_to_store);		
				} catch (Exception e) {
				}

			}
		}

	}

	public static void download (String url,String path_to_store){

		if (Files.exists(Paths.get(path_to_store))){
			return;

		}
		Path path = Paths.get(path_to_store);
		System.out.println(path);

		URI uri = URI.create(url);
		try (InputStream in = uri.toURL().openStream()) {
			Files.copy(in, path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Write_To_CSV_File(){

		File dir = new File(directory_name+"Output");
		dir.mkdir();

		String output_path = directory_name + "Output" + "\\" +"output.csv";
		try {
			file_writer = new FileWriter(output_path);
			initial_setup_csv();
			parse_text_to_csv();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void initial_setup_csv() throws IOException{

		file_writer.append("region").append(",");
		file_writer.append("weather").append(",");
		file_writer.append("year").append(",");
		file_writer.append("keys").append(",");
		file_writer.append("values").append(",");
		file_writer.append(System.getProperty("line.separator"));
	}

	//This function opens the downloaded text files one by one and parse the text files into .csv file format.
	public static void parse_text_to_csv() throws IOException{
		for(String region : regions){

			for(String param : params){

				String file_path_to_parse = directory_name + region + "\\" + region + "_" + param + ".txt";

				try {
					BufferedReader reader = new BufferedReader(new FileReader(file_path_to_parse));

					int line_count = 1;
					while((line = reader.readLine()) != null){

						//This condition parses the name of the Months in a year and store them in keys[] array
						if(line_count == 8){
							storeKeys(line);
						}
						//This will parse the remaining text file and append the data into the .csv file format.
						else if(line_count > 8){
							parseRemainingLines(line,region,param);
						}
						line_count++;
					}

					reader.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}


			}
		}
	}

	//This function will accept the line as a parameter and split the line around white spaces and store the
	//values in the String[] array. 
	public static void storeKeys(String line){
		keys = line.split("\\s{1,5}");
	}

	public static void parseRemainingLines(String line,String region,String param){

		String[] values = line.split("\\s{1,6}");
		String year = values[0];
		for (int i = 1; i < values.length; i++) {
			if (" ---  ".equals(values[i]) ||  "".equals(values[i])) {
				values[i] = NOT_AVAILABLE;
			}else{
				try {
					file_writer.append(region).append(",");
					file_writer.append(param).append(",");
					file_writer.append(year).append(",");
					file_writer.append(keys[i]).append(",");
					file_writer.append(values[i]).append(",");
					file_writer.append(System.getProperty("line.separator"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//This function will close the Opened file.
	private static void closing_files() {
		// TODO Auto-generated method stub

		try {
			file_writer.flush();
			file_writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub

		Download_Weather_Report_Files();
		Write_To_CSV_File();
		closing_files();
	}


}
