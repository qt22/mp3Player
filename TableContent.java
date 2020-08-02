package MusicPlayer;
/*
 *  Jerry Tian
 *  ICS3U1
 *  Jan 21, 2019
 *  For Mr. Radulovic
 *  Culminating Assignment
 *  This class is the music player GUI. It plays both mp3 files and wav files
 *  It contains all the buttons, textfields and a table, etc.
 */
import javafx.beans.property.SimpleStringProperty;

public class TableContent{
		private SimpleStringProperty SongsName;
		private SimpleStringProperty ArtistsName;
		//  SimpleStringProperty class provides a full implementation of a Property wrapping a String value.
		public TableContent(String songsname, String artistsname) {
			this.SongsName = new SimpleStringProperty(songsname);
			this.ArtistsName = new SimpleStringProperty(artistsname);
		}
		
		public String getSongsName() {
			return SongsName.get();
		}
		
		public void setSongsName(String songsName) {
			SongsName.set(songsName);
		}
		
		public String getArtistsName() {
			return ArtistsName.get();
		}
		
		public void setArtistsName(String artistsName) {
			ArtistsName.set(artistsName);
		}
		// getters and setters method for changing texts in the table in the mp3GUI class.
	}
