package MusicPlayer;

/*
 *  Jerry Tian
 *  ICS3U1
 *  Jan 21, 2019
 *  For Mr. Radulovic
 *  Culminating Assignment
 *  This class is the music player GUI. It plays both mp3 files and wav files
 *  It contains all the buttons, textfields and a table, etc.
 *  I put the sources of my music in the user menu pdf
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class mp3GUI extends Application {
	// I define all variables private because I'm not using these variables in other classes
	private int songposition = 0;  
	// songposition is the index of the songsList arraylist 
	
	private Label start, end, nowplaying, artist;
	
	private Label[] checkbox = new Label[2]; // Labels for checkboxes;
	private CheckBox random, repeat;
	
	private Slider slider;
	private ProgressBar progressbar;
	private AnimationTimer timer;
	
	private File record;
	
	private boolean closeEvent = false;
	
	private String Start = "", End = "";
	private ArrayList <String> songsname = new ArrayList<String>(), 
			artistsname= new ArrayList<String>(),
			songspath = new ArrayList<String>();
	private ArrayList<MediaPlayer> songsList = new ArrayList<MediaPlayer>(); 
	
	private ObservableList<TableContent> data = FXCollections.observableArrayList();
	// the table shows the song list
	
	public static void main(String[] args) {
		launch(args); // launch the GUI
	}
	
	public void start(Stage musicPlayer) throws Exception {
		record = new File("record.csv");
		/* 
		 * Every time the user opens the window, it creates a csv file at the default location.
		 * If the file already exists, it doesn't replace it but opens it instead.
		 */
		
		double WindowWidth = 700, WindowHeight = 400, 
		columnWidth = 250, textfieldWidth = 150, buttonWidth = 80,
		progressWidth = 150, VSpacing = 20, HSpacing = 10;
		// the size of nodes (avoid magic numbers)
		String title = "Music Player";
		// the title of the GUI window
		
		VBox screen = new VBox(); 
		screen.setSpacing(VSpacing);
		// The amount of vertical space between each nodes in the vbox screen is 20.
		screen.setPadding(new Insets(10));
		Scene scene = new Scene(screen, WindowWidth, WindowHeight);
		
		HBox[] hbs = new HBox[5];
		for(int i = 0; i < hbs.length; i++) {
			hbs[i] = new HBox();
			hbs[i].setSpacing(HSpacing);
			hbs[i].setAlignment(Pos.CENTER);
		}
		/* 
		 * I create an array of HBoxes because I don't need to define them using five lines.
		 * The amount of horizontal space between each nodes in the hboxes is 10.
		 * All nodes are positioned on center both vertically and horizontally.
		 */
		screen.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ESCAPE) {
					musicPlayer.close();
				}
			}
		});
		// If the Esc key is pressed, close the GUI window.
		
		TableView<TableContent> SongList = new TableView<TableContent>(); 
		SongList.setMaxWidth(WindowWidth);
		SongList.setEditable(true);
		/*
		 * Create a table with no content
		 * The user can edit it since I set editable = true. 
		 */
		
		TableColumn names = new TableColumn("Songs Name");
		names.setMinWidth(columnWidth);
		names.setCellValueFactory(
				new PropertyValueFactory<TableContent, String>("SongsName"));
		// It gets the value from the TableContent class by using the variable SimpleStringProperty SongsName.
		names.setCellFactory(TextFieldTableCell.forTableColumn());
		// If the user wants to edit the content in the table, he or she can type in a textfield to edit.
		names.setOnEditCommit(
				new EventHandler<CellEditEvent<TableContent, String>>(){
					@Override
					public void handle(CellEditEvent<TableContent, String> event) {
						int row = event.getTablePosition().getRow();
						((TableContent)	event.getTableView().getItems().get(row))
						.setSongsName(event.getNewValue());
						/*
						 *  By calling the setter method in TableContent class, 
						 *  it changes the content in the table at the specific row.
						 */
						songsname.remove(row);
						songsname.add(row, event.getNewValue());
						// It also replaces the content in the songsname ArrayList.
					}
				});
					
		TableColumn artists = new TableColumn("Artists Name");
		artists.setMinWidth(columnWidth);
		artists.setCellValueFactory(
				new PropertyValueFactory<TableContent, String>("ArtistsName"));
		// It gets the value from the TableContent class by using the variable SimpleStringProperty ArtistsName.
		artists.setCellFactory(TextFieldTableCell.forTableColumn());
		// If the user wants to edit the content in the table, he or she can type in a textfield to edit.
		artists.setOnEditCommit(
				new EventHandler<CellEditEvent<TableContent, String>>(){
					@Override
					public void handle(CellEditEvent<TableContent, String> event) {
						int row = event.getTablePosition().getRow();
						((TableContent)	event.getTableView().getItems().get(row))
						.setSongsName(event.getNewValue());
						/*
						 *  By calling the setter method in TableContent class, 
						 *  it changes the content in the table at the specific row.
						 */
						artistsname.remove(row);
						artistsname.add(row, event.getNewValue());
						// It also replaces the content in the artistsname ArrayList.
					}
				});
		
		SongList.setItems(data);
		// Set the content of the table by using a observable list.
		SongList.getColumns().addAll(names, artists);
		// Add columns to the table.
		
		/*
		 *  for the table part, I used this website to help me:
		 *  https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
		 */
		
		
		TextField tf1 = new TextField();
		tf1.setPromptText("music file path");
		// Tell the user what this textfield is used for.
		tf1.setPrefWidth(textfieldWidth);
		
		TextField tf2 = new TextField();
		tf2.setPromptText("artist name");
		// Tell the user what this textfield is used for.
		tf2.setPrefWidth(textfieldWidth);
		
		Button add_songs = new Button("Add");
		add_songs.setPrefWidth(buttonWidth);
		add_songs.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				File musicfile = new File(tf1.getText());
				Media media = new Media(musicfile.toURI().toString());
				MediaPlayer playsong = new MediaPlayer(media);			
				// open the file and creates an instance MediaPlayer playsong for later use
				
				String songname = musicfile.getName();
				String finalsongname = songname.substring(0, songname.indexOf('.'));
				// since the name of all music files are like name.mp3 or name.wav
				// I use substring to get rid of the ".mp3" suffix.
				
				data.add(new TableContent(finalsongname, tf2.getText()));
				// add songname and artistsname by calling the constructor method in TableContent Class
				songsList.add(playsong); // MediaPlayer arraylist
				songspath.add(tf1.getText());	// song path (for example, C:\\music.mp3)
				songsname.add(finalsongname);	
				artistsname.add(tf2.getText());
				
				tf1.clear();
				tf2.clear();
				// They are cleared for the next input
			}
		});
		
		Button play = new Button("Play");
		play.setPrefWidth(buttonWidth);
		play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Playsong();
				// The play button calls the Playsong method to play the music file.
			}
		});
		
		Button pause = new Button("Pause");
		pause.setPrefWidth(buttonWidth);
		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				songsList.get(songposition).pause();
				timer.stop();
				// It pauses the music and the timer as well.
			}
		});
		
		Button previous = new Button("Previous");
		previous.setPrefWidth(buttonWidth);
		previous.setOnAction(new EventHandler <ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				songsList.get(songposition).stop();
				timer.stop();
				//It stops the both the song and the timer.
				if(songposition > 0) {
					songposition--;
					// If the current song is not the first song in the arraylist, it minus one 
				}
				else {
					songposition = songsList.size()-1;
					// If the current song is the first song in the arraylist, it goes to the last song in the arraylist
				}
				Playsong();
				// call the playsong method to play the song and start a new timer
			}
		});
		
		Button next = new Button("Next");
		next.setPrefWidth(buttonWidth);
		next.setOnAction(new EventHandler <ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				songsList.get(songposition).stop();
				timer.stop();
				//It stops the both the song and the timer.
				if(songposition < songsList.size()-1) {
					songposition++;
					// If the current song is not the last song in the arraylist, it adds one 
				}else {
					songposition = 0;
					// If the current song is the last song in the arraylist, it goes to the first song in the arraylist
				}
				Playsong();
				// call the playsong method to play the song and start a new timer
			}
		});
		
		nowplaying = new Label();
		artist = new Label();	// define labels 
		
		progressbar = new ProgressBar(0);
		// The progressbar starts at 0.
		progressbar.setPrefWidth(progressWidth);		

		random = new CheckBox();
		checkbox[0]= new Label("Random");
		// it random chooses the music from the songsList arraylist when the current music ends
		repeat = new CheckBox();
		checkbox[1]= new Label("Repeat");
		// it repeats the current music again when the current music ends
		
		start = new Label();
		end = new Label();// define labels 
		
		slider = new Slider(0, 1, 0); // the range of the slider is from 0 to 1, the current value is 0
		slider.setPrefWidth(progressWidth);
		slider.valueProperty().addListener(new InvalidationListener(){
			@Override
			public void invalidated(Observable arg0) {
					progressbar.setProgress(slider.getValue());
					// When the slider is moved by the mouse, the progress bar changes the value as well.
					Duration totalduration = songsList.get(songposition).getTotalDuration();
					songsList.get(songposition).seek(Duration.millis(totalduration.toMillis()*
							slider.getValue()));
					// When the slider is moved by the mouse, the song seeks a new location.
			}
		});
		
		Label Volume = new Label("Volume");
		Slider volume = new Slider(0, 1, 0);// the range of the slider is from 0 to 1, the current value is 0
		volume.setPrefWidth(slider.getWidth()/2);
		volume.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				for(int i = 0; i < songsList.size(); i++) {
					songsList.get(i).setVolume(volume.getValue());
					// When the volume slider changes, all songs changes the volume.
				}
			}
		});
		
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setContent(SongList);	// add the scroll bar to the table
		scrollpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);	
		//	Horizontal scroll bar should be shown when required.
		scrollpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		// Vertical scroll bar should be shown when required.
			
		Button loadLastTime = new Button("Reload");
		loadLastTime.setPrefWidth(buttonWidth);
		loadLastTime.setOnAction(new EventHandler <ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Scanner scan = null;
				try {
					scan = new Scanner(record);
					// a scanner which read the file
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				songspath.clear();
				songsList.clear();
				songsname.clear();
				artistsname.clear();
				// clear all the GUI has now in order to load last time's data
				if(scan.hasNextLine()) {	// If the file doesn't reach the end
					String[] songInfo = scan.nextLine().split(",");
					// since it is a csv file, it should be separated by comma for future use
					for(int i = 0; i < songInfo.length; i++) {
						/*
						 *  The first line of the file is
						 *   songpath, songname, artistname, songpath2, songname2, artistname2, etc.
						 */
						if(i % 3 == 0) { // songpaths
							File musicfile = new File(songInfo[i]);
							Media media = new Media(musicfile.toURI().toString());
							MediaPlayer playsong = new MediaPlayer(media);				
							songsList.add(playsong);
							songspath.add(songInfo[i]);// add them back to the arraylists
						}
						if((i-1) % 3 == 0) {	//songnames
							songsname.add(songInfo[i]);// add them back to the arraylist
						}
						if((i - 2) % 3 == 0) {// artistnames
							artistsname.add(songInfo[i]);// add them back to the arraylist
						}
					}
					for(int i = 0; i < songsname.size(); i++) {
						data.add(new TableContent(songsname.get(i), artistsname.get(i)));
						// add them back to the table too
					}
					songposition = Integer.parseInt(scan.nextLine());
					Playsong();
					/*
					 * The second line is where the song stops .
					 * By calling the Playsong method, it automatically plays the song 
					 * which wasn't finished last time and it plays at the start of the song.
					 */
				}
			}
		});
		
		Button clear = new Button("Clear");
		clear.setPrefWidth(buttonWidth);
		clear.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				try {
					PrintWriter writer = new PrintWriter(record);	// help to write the method
					writer.print("");	
					// when the clear button is pressed, it writes nothing to the file (clean the file)
					writer.close();	
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				data.clear();
				songspath.clear();
				songsList.clear();
				songsname.clear();
				artistsname.clear();	// clear all the arraylists and the table
				closeEvent = false;	// It won't save anything while close the window.
			}
		});
		
		hbs[0].getChildren().add(scrollpane);
		hbs[1].getChildren().addAll(tf1, tf2, add_songs, loadLastTime, clear);
		hbs[2].getChildren().addAll(play, pause, previous, next, checkbox[0], random, checkbox[1], repeat);
		hbs[3].getChildren().addAll(nowplaying, progressbar, artist);
		hbs[4].getChildren().addAll(start, slider, end, Volume,volume);
		screen.getChildren().addAll(hbs[0], hbs[1], hbs[2], hbs[3], hbs[4]);
		// Add children to the hboxes first and add all hboxes the screen Vbox.
		
		musicPlayer.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	if(closeEvent) { //if the closeEvent is true
            		PrintWriter writer = null;
            		Scanner scan = null;
            		StringBuilder builder = new StringBuilder(); // The StringBuilder helps to write to the csv file.
	                try {
						writer = new PrintWriter(record);
						scan = new Scanner(record);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(scan.hasNextLine()) {
						writer.print("");
						// It overwrites the file before 
					}
					for(int i = 0; i < songspath.size(); i++) {
						if(i != songspath.size() - 1) {
							builder.append(songspath.get(i) + "," + songsname.get(i) + "," + artistsname.get(i) + ",");
							// if this is not the end of the songpath file, add a comma at the end 
						}else {
							builder.append(songspath.get(i) + "," + songsname.get(i) + "," + artistsname.get(i));
							// if it is the end, it doesn't need a comma at the end (because this is a csv file)
						}
					}
					builder.append('\n'); // go to the next line
					builder.append(songposition);	// add the songposition value to the StringBuilder.
					
					writer.write(builder.toString());	// write all stuff in the StringBuilder to the csv file
					
					writer.close();
					scan.close();	// close the PrintWriter and the Scanner
            	}
            }
        });
		musicPlayer.setTitle(title);
		musicPlayer.setScene(scene); 
		musicPlayer.show();	// show the window
	}
	private void Playsong() {
		songsList.get(songposition).play();	// Get the song to play first
		
		timer = new AnimationTimer() {
			long oldTime = 0, newTime = 100000000;
			public void handle(long time) {
				if(time - oldTime > newTime) {	// update the program every tenth of a second
					double currentTime = songsList.get(songposition).getCurrentTime().toMillis();
					progressbar.setProgress(currentTime	
							/songsList.get(songposition).getTotalDuration().toMillis());
					// every tenth of a second it updates the progressbar to get it moving
					durationText(); 
					// every tenth of a second it updates the songname, artistname, currentTime, endtime.
					if(currentTime >= songsList.get(songposition).
							getTotalDuration().toMillis()) {	// When the music ends
						progressbar.setProgress(0);	// reset the value of the progress bar to zero
						if(repeat.isSelected()) {
							songposition--;
							/*
							 *  If the repeat checkbox is selected, 
							 *  the songposition is minus by one because later it needs to be add by one anyway.
							 */
						}
						if(random.isSelected()) {
							Random rand = new Random();	
							songposition = rand.nextInt(songsList.size());
							// Random generate the next songposition
						}
						if(songposition < songsList.size() - 1) {
							songposition++;	// if this is not the last song, the song moves on
						}else {
							songposition = 0;	// if this is the last song, it goes to the first song.
						}
						songsList.get(songposition).play(); // play the song at the updated songposition
					}
					oldTime = time; // update the program every tenth of a second
				}
			}
		};
		timer.start();	// start the timer
		closeEvent = true;	// when the window is closed, it saves the data to a file
	}
	
	private void durationText() {
		int[][] time = new int[2][2];
		// int[start & end][minute and then second]
		
		// format the time to the clock format (like time on the phone)
		time[0][0] = (int) songsList.get(songposition).getCurrentTime().toSeconds() / 60; 
		// minute(s) of the current time
		time[0][1] = (int) songsList.get(songposition).getCurrentTime().toSeconds() % 60;
		// second(s) of the current time 
		if(time[0][1] < 10) {
			Start = time[0][0] + ":0" + time[0][1];
			// if the second of the current time is less than 10, add a zero after colon to format it
		}else {
			Start = time[0][0] + ":" + time[0][1];
			// add a colon in between to format the time
		}
		
		// format the time to the clock format (like time on the phone)
		time[1][0] = (int) songsList.get(songposition).getTotalDuration().toSeconds() / 60;
		// minute(s) of the end time
		time[1][1] = (int) songsList.get(songposition).getTotalDuration().toSeconds() % 60;
		// second(s) of the end time 
		if(time[1][1] < 10) {
			End = time[1][0] + ":0" + time[1][1];
			// if the second of the end time is less than 10, add a zero after colon to format it
		}else {
			End = time[1][0] + ":" + time[1][1];
			// add a colon in between to format the time
		}
		
		start.setText(Start);
		end.setText(End);	
		nowplaying.setText("Now Playing: "  + songsname.get(songposition));
		artist.setText("Artist: " + artistsname.get(songposition));
		// set the text of the labels when this method is called
	}
}
