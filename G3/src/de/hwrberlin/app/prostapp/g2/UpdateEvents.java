package de.hwrberlin.app.prostapp.g2;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author M. Funk and P. K�hn
 *
 */
public class UpdateEvents implements ITblStudentEvent {
  
  final static String TAG = "Klasse: UpdatEvents";
  static InitDB initDB; //Static, da f�r alle Instanzen


  
public void checkEvents(Context context, CalendarSample calendarSample, int calIndex){
    UpdateEvents.initDB = new InitDB(context);     
    Cursor cur = initDB.getReadableDatabase().rawQuery("SELECT * FROM " + TBL_EVENTS, null);      
    Log.d(TAG, "SQL-Select done");
    
    if (cur.isBeforeFirst()){
        while( cur.moveToNext() ){  
          Log.d(TAG, "Event ID in DB = " + cur.getString(cur.getColumnIndex(COL_G_EVENT_ID)));         
            if (cur.isNull(cur.getColumnIndex(COL_G_EVENT_ID)) )             
            {
              //Event-Infos aus DB auslesen und �bergeben                          
              String sVlName = cur.getString(cur.getColumnIndex(COL_EVENT_NAME));
              String sStartDate = cur.getString(cur.getColumnIndex(COL_START_DATE)); //Datums�bergabe f�r AddEvent
              String sEndDate = cur.getString(cur.getColumnIndex(COL_END_DATE));
              String sFrequence = cur.getString(cur.getColumnIndex(COL_REC_FREQUENCE)); 
              //Event einf�gen
              new AsyncAddEvent2(calendarSample, calIndex, sVlName, sStartDate, sEndDate, sFrequence).execute(); 
            }
            else Log.d(TAG, "Event bereits im Kalender. ID: " + cur.getString(cur.getColumnIndex(COL_G_EVENT_ID)));         
        }
    }
    else {
        Log.i( "DB", "DB leer." );
        } 
    
//    L�schen eines events
//    new AsyncDeleteEvent(calendarSample, calIndex, "b89kgs5mocrgsj6fga31mo5btc").execute();
//    new AsyncDeleteEvent(calendarSample, calIndex, "lk4prfipfralhlfhmh1o7mv520").execute(); 
//    new AsyncDeleteEvent(calendarSample, calIndex, "35lesclpuetvab7rahi3elol3g").execute(); 
    
//    �ndern eines events
//    new AsyncChangeEvent(calendarSample, calIndex, "Ge�nderte Vorlesung", "2013-01-15T15:30:00+00:00", "2013-01-15T16:30:00+00:00", "", "27mjsetgl03icsvo5pt2hjnbv8").execute();
    
cur.close();
initDB.close();
  
  }
public void addEventId(String eventId, String vlName){
  String sqlquery = "UPDATE " + TBL_EVENTS + 
                     " SET " + COL_G_EVENT_ID + " = '" + eventId +  
                     "' WHERE " + COL_EVENT_NAME + " = '" + vlName + "';"; 
  Log.d( "Query", sqlquery );
  initDB.getWritableDatabase().execSQL(sqlquery);
  }  

public void deleteEvent(String eventId){
  String sqlquery = "DELETE FROM " + TBL_EVENTS + 
                    " WHERE " + COL_G_EVENT_ID + " = '" + eventId + "';"; 
  Log.d( "Query", sqlquery );
  initDB.getWritableDatabase().execSQL(sqlquery);
  } 

public void changeEvent(String eventId, String sVlName, String sStartDate, String sEndDate, String sFrequency){
  String sqlquery =  "UPDATE " + TBL_EVENTS + 
                    " SET " + COL_EVENT_NAME      + " = '" + sVlName +    "', "
                            + COL_START_DATE      + " = '" + sStartDate + "', "
                            + COL_END_DATE        + " = '" + sEndDate + "', " 
                            + COL_REC_FREQUENCE   + " = '" + sFrequency + "'"
                    + " WHERE " + COL_G_EVENT_ID  + " = '" + eventId + "';"; 
  Log.d( "Query", sqlquery );
  initDB.getWritableDatabase().execSQL(sqlquery);
  } 
}


